package com.cpen391.module2.hikingpal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.cpen391.module2.hikingpal.module.Announcement;
import com.cpen391.module2.hikingpal.module.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HikingPalStorage {

    FileOutputStream fos;
    DataOutputStream dos;

    String mapImage = "MapImages";
    String messagesRoot = "Messages";
    String announcementsRoot = "Announcements";

    public final char BT_MAP_INIT = 'X';
    public final char BT_MAP_DELIMITER = 'Q';
    public final char BT_MAP_FIELD_DELIMITER = 'U';

    Context context;

    private final static String storage = "storage.json";
    private final static String messages = "messages.json";
    private final static String announcements = "announcements.json";

    private SharedPreferences settings;

    public HikingPalStorage(Context context){
        this.context = context;
        this.settings = context.getSharedPreferences("prefs", 0);
    }

    public List<String> getMapImageList() {
        String[] mapImageArray = {mapImage};
        return new ArrayList<String>(Arrays.asList(mapImageArray));
    }

    public List<String> getMessageList() {
        String[] messageArray = {messages};
        return new ArrayList<String>(Arrays.asList(messageArray));
    }

    public List<String> getAnnouncementList() {
        String[] announcementArray = {announcements};
        return new ArrayList<String>(Arrays.asList(announcementArray));
    }

    public void setUp() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            write(outputStreamWriter, makeRoot(getMapImageList()).toString());
            OutputStreamWriter outputStreamWriterMessages = new OutputStreamWriter(context.openFileOutput(messages, Context.MODE_PRIVATE));
            write(outputStreamWriterMessages, makeRoot(getMessageList()).toString());
            OutputStreamWriter outputStreamWriterAnnouncements = new OutputStreamWriter(context.openFileOutput(announcements, Context.MODE_PRIVATE));
            write(outputStreamWriterAnnouncements, makeRoot(getAnnouncementList()).toString());

        } catch (FileNotFoundException e) {}
    }

    private JSONObject makeRoot(List<String> rootList) {
        JSONObject root =  new JSONObject();
        try{
            for(int i = 0; i < rootList.size(); i++){
                root.put(rootList.get(i), new JSONArray());
            }
        } catch (JSONException e) {}

        return root;
    }

    public void writeToStorage(int imageId, int subscribe, long myDuration, long myDistance, List<String> mySpots, String myDate, int myRating, String pathToImage){

        JSONObject element = create(imageId, subscribe, myDuration, myDistance, mySpots, myDate, myRating, pathToImage);

        String jsonRoot = readFile(storage);
        try {
            write(new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE)), add_to_root(element, jsonRoot, mapImage));
        } catch (FileNotFoundException e) {}
    }

    public void writeToMessages(int id, int sender, String content) {

        JSONObject element = createMessage(id, sender, content);
        String jsonRoot = readFile(messages);
        try {
            write(new OutputStreamWriter(context.openFileOutput(messages, Context.MODE_PRIVATE)), add_to_root(element, jsonRoot, messagesRoot));
        } catch (FileNotFoundException e) {}

    }

    private JSONObject createMessage(int id, int sender, String content) {
        JSONObject element = new JSONObject();
        try {
            element.put("id", id);
            element.put("sender", sender);
            element.put("content", content);

        } catch (JSONException e) {}
        return element;

    }

    public void writeToAnnouncements(int id, String content, String title){

        JSONObject element = createAnnoucement(id, content, title);
        String jsonRoot = readFile(announcements);
        try {
            write(new OutputStreamWriter(context.openFileOutput(announcements, Context.MODE_PRIVATE)), add_to_root(element, jsonRoot, announcementsRoot));
        } catch (FileNotFoundException e) {}
    }

    private JSONObject createAnnoucement(int id, String content, String title) {
        JSONObject element = new JSONObject();
        try {
            element.put("id", id);
            element.put("content", content);
            element.put("title", title);
        } catch (JSONException e) {}
        return element;
    }

    private JSONObject create(int imageId, int subscribe,  long myDuration, long myDistance, List<String> mySpots, String myDate, int myRating, String pathToImage) {
        JSONObject element = new JSONObject();
        try {
            element.put("imageId", imageId);
            element.put("subscribe", subscribe);
            element.put("myDuration", myDuration);
            element.put("myDistance", myDistance);
            JSONArray array = new JSONArray(mySpots);
            element.put("mySpots", array);
            element.put("myDate", myDate);
            element.put("myRating", myRating);
            element.put("pathToImage", pathToImage);
        } catch (JSONException e) {}
        return element;

    }

    private String readFile(String storageType){
        InputStreamReader isr;
        try{
            isr = new InputStreamReader(context.openFileInput(storageType));
            return read(isr);
        } catch (FileNotFoundException e){

        }

        return "";
    }

    private String read(InputStreamReader isr){
        assert(isr != null);

        String root = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(isr);
            String receiveString = "";
            StringBuilder stringBuilder  = new StringBuilder();

            while((receiveString = bufferedReader.readLine()) != null){
                stringBuilder.append(receiveString);
            }
            isr.close();
            root = stringBuilder.toString();
        }catch (IOException e) {}

        return root;
    }

    private String add_to_root(JSONObject element, String root, String type) {
        assert(element != null);
        try {
            JSONObject jsonRootObject;
            JSONArray jsonArray;
            if (root != "") {
                jsonRootObject = new JSONObject(root);
                jsonArray = jsonRootObject.optJSONArray(type);
                if (jsonArray != null) {
                    jsonArray.put(element);
                }
                else {
                    jsonArray = new JSONArray();
                    jsonArray.put(element);
                    jsonRootObject.put(type, jsonArray);
                }
            }
            else {
                jsonRootObject = new JSONObject();
                jsonArray = new JSONArray();
                jsonArray.put(element);
                jsonRootObject.put(type, jsonArray);
            }

            return jsonRootObject.toString();

        } catch (JSONException e) {}

        return null;
    }

    private void write(OutputStreamWriter osw, String word) {
        try {
            osw.write(word);
            osw.close();
        } catch (IOException e){}
    }

    public JSONArray getArray(String restaurant) {
        String root = readFile(storage);
        return extractArray(root, restaurant);
    }

    private JSONArray extractArray(String root, String attribute) {
        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            JSONArray jsonArray = jsonRootObject.optJSONArray(attribute);

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    public String getMapImage(String mapImagePath, boolean single) {

        StringBuilder sb = new StringBuilder();
        if(single) {
            sb.append(BT_MAP_INIT);
        }

        JSONObject jobject = new JSONObject();
        try {
            jobject.put("mapPath", mapImagePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = getObject(jobject);
        JSONArray arr = jsonObject.optJSONArray("mySpots");
        List<String> list = new ArrayList<String>();
        int j;
        for(j = 0; j < arr.length(); j++){
            try {
                list.add(arr.get(j).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String object = GetDataString(jsonObject.optInt("imageId"), jsonObject.optInt("subscribe"), jsonObject.optInt("myRating"),
                jsonObject.optLong("myDistance"), jsonObject.optLong("myDuration"),
                list, jsonObject.optString("myDate"));
        sb.append(object);

        if(single) {
            sb.append(BT_MAP_INIT);
        }
        return sb.toString();
    }

    public String getMapImage(int mapImageId) {

        StringBuilder sb = new StringBuilder();
        sb.append(BT_MAP_INIT);
        JSONObject jsonObject = getObject(mapImageId);
        JSONArray arr = jsonObject.optJSONArray("mySpots");
        List<String> list = new ArrayList<String>();
        int j;
        for(j = 0; j < arr.length(); j++){
            try {
                list.add(arr.get(j).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String object = GetDataString(jsonObject.optInt("imageId"), jsonObject.optInt("subscribe"), jsonObject.optInt("myRating"),
                jsonObject.optLong("myDistance"), jsonObject.optLong("myDuration"),
                list, jsonObject.optString("myDate"));
        sb.append(object);

        sb.append(BT_MAP_INIT);
        return sb.toString();
    }

    //// TODO: 2017-04-03  
    public List<Message> getAllMessages() {
        JSONArray jsonArray = extractArray(readFile(messages), messagesRoot);
        List<Message> messageList = new ArrayList<Message>();
        int i;
        if(jsonArray==null){
            return null;
        }
        else {
            for (i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject element = jsonArray.getJSONObject(i);
                    Message message = new Message(element.optLong("id"), element.optString("content"), element.optInt("sender"));
//                message.setContent(element.optString("content"));
//                message.setId(element.optLong("id"));
//                message.setSender(element.optInt("sender"));
                    messageList.add(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return messageList;
    }

    public List<Announcement> getAllAnnoucements() {
        JSONArray jsonArray = extractArray(readFile(announcements), announcementsRoot);
        List<Announcement> announcementList = new ArrayList<Announcement>();
        int j;
        if (jsonArray == null) return null;
        for(j = 0; j < jsonArray.length(); j++){
            try {
                JSONObject element = jsonArray.getJSONObject(j);
                Announcement announcement = new Announcement();
                announcement.setContent(element.optString("content"));
                announcement.setId(element.optLong("id"));
                announcement.setTitle(element.optString("title"));
                announcementList.add(announcement);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return announcementList;
    }

    public Message getMessage(long id){

        String root = readFile(messages);
        JSONObject jobject =  extractObject(root, id, "id");
        Message message = new Message(jobject.optLong("id"),jobject.optString("content"),jobject.optInt("sender"));
//        message.setContent(jobject.optString("content"));
//        message.setId(jobject.optLong("id"));
//        message.setSender(jobject.optInt("sender"));
        return message;
    }

    public Announcement getAnnouncment(long id){

        String root = readFile(announcements);
        JSONObject jobject =  extractObject(root, id, "id");
        Announcement announcement = new Announcement();
        announcement.setContent(jobject.optString("content"));
        announcement.setId(jobject.optLong("id"));
        announcement.setTitle(jobject.optString("title"));
        return announcement;
    }

    public void removeAllMessage() {
        String root = readFile(messages);
        if(root == "") return;
        JSONArray jsonArray = extractArray(readFile(messages), messagesRoot);
        try {
            int i;
            for(i=0;i<jsonArray.length();i++) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(messages, Context.MODE_PRIVATE));
                write(outputStreamWriter, removeFromStorage(root, messagesRoot, i+1).toString());
                Log.d("del", String.valueOf(i));
            }
        } catch (FileNotFoundException e) {}

    }

    public void removeAnnouncement(long id) {

        String root = readFile(announcements);
        if(root == "") return;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(announcements, Context.MODE_PRIVATE));
            write(outputStreamWriter, removeFromStorage(root, announcements, id).toString());
        } catch (FileNotFoundException e) {}

    }

    public JSONObject getObject(int mapImageId){

        String root = readFile(storage);
        return extractObject(root, mapImageId, "imageId");
    }

    private JSONObject extractObject(String root, int mapImageId, String fieldType) {

        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(mapImage);

            int i = 0;
            for(i = 0; i < jsonArray.length(); i++){
                JSONObject element = jsonArray.getJSONObject(i);
                if(element.optInt(fieldType) == mapImageId){
                    return element;
                }
            }
        } catch (JSONException e) {}

        return null;
    }

    private JSONObject extractObject(String root, long id, String fieldType) {

        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(mapImage);

            int i = 0;
            for(i = 0; i < jsonArray.length(); i++){
                JSONObject element = jsonArray.getJSONObject(i);
                if(element.optLong(fieldType) == id){
                    return element;
                }
            }
        } catch (JSONException e) {}

        return null;
    }

    public JSONObject getObject(String mapPath) {

        String root = readFile(storage);
        return extractObject(root, mapPath, "pathToImage");
    }



    public JSONObject getObject(JSONObject jsonObject) {

        String root = readFile(storage);
        return extractObject(root, jsonObject);
    }

    private JSONObject extractObject(String root, String mapImageId, String fieldType) {

        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(mapImage);

            int i = 0;
            for(i = 0; i < jsonArray.length(); i++){
                JSONObject element = jsonArray.getJSONObject(i);
                if(element.optString(fieldType).equals(mapImageId)){
                    return element;
                }
            }
        } catch (JSONException e) {}

        return null;
    }

    private JSONObject extractObject(String root, JSONObject jsonObject) {

        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(mapImage);

            int i = 0;
            for(i = 0; i < jsonArray.length(); i++){
                JSONObject element = (JSONObject) jsonArray.get(i);
                String mapPath = jsonObject.optString("mapPath");
                String pathToImage = element.optString("pathToImage");
                if(mapPath.contains(pathToImage)){
                    return element;
                }
            }
        } catch (JSONException e) {}

        return null;
    }

    public String getAllMapImages(){

        JSONArray jsonArray = extractArray(readFile(storage), mapImage);
        StringBuilder sb = new StringBuilder();
        sb.append(BT_MAP_INIT);

        int i = 0;

        for(i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray arr = jsonObject.optJSONArray("mySpots");
                List<String> list = new ArrayList<String>();
                int j;
                for(j = 0; j < arr.length(); j++){
                    list.add(arr.get(j).toString());
                }
                String object = GetDataString(jsonObject.optInt("imageId"), jsonObject.optInt("subscribe"), jsonObject.optInt("myRating"),
                        jsonObject.optLong("myDistance"), jsonObject.optLong("myDuration"),
                        list, jsonObject.optString("myDate"));

                sb.append(object);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        sb.append(BT_MAP_INIT);
        return sb.toString();
    }

    public String GetDataString(int myName, int subscribe, int myRating, long myDistance, long myDuration, List<String> mySpots, String myDate){
        StringBuilder sb = new StringBuilder();
        sb.append(BT_MAP_DELIMITER);
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(myName);
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(myRating);
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(myDistance);
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(myDuration);
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(TextUtils.join(",", mySpots));
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(myDate);
        sb.append(BT_MAP_FIELD_DELIMITER);
        sb.append(BT_MAP_DELIMITER);
        return sb.toString();
    }

    public void removeMapImage(String path) {

        String root = readFile(storage);
        if(root == "") return;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            write(outputStreamWriter, removeFromStorage(root, mapImage, path).toString());
        } catch (FileNotFoundException e) {}
    }


    private JSONObject removeFromStorage(String root, String array, String path){

        try {
            if (root != "") {
                JSONObject jsonRootObject = new JSONObject(root);
                JSONArray jsonArray = jsonRootObject.optJSONArray(array);
                JSONObject jsonNewRoot = new JSONObject();
                JSONObject jobject = new JSONObject();
                try {
                    jobject.put("mapPath", path);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray newArray = new JSONArray();
                boolean remove = true;
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(!jobject.optString("mapPath").equals(jsonArray.getJSONObject(i).optString("pathToImage").toString())){
                        newArray.put(jsonArray.get(i));
                    }
                    else {
                        remove = false;
                    }
                }
                jsonNewRoot.put(array, newArray);
                return jsonNewRoot;
            }

        }  catch (JSONException e) {}

        return null;
    }

    private JSONObject removeFromStorage(String root, String array, long id){

        try {
            if (root != "") {
                JSONObject jsonRootObject = new JSONObject(root);
                JSONArray jsonArray = jsonRootObject.optJSONArray(array);
                JSONObject jsonNewRoot = new JSONObject();
                JSONArray newArray = new JSONArray();
                boolean remove = true;
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(id == jsonArray.getJSONObject(i).optLong("id")){
                        newArray.put(jsonArray.get(i));
                    }
                    else {
                        remove = false;
                    }
                }
                jsonNewRoot.put(array, newArray);
                return jsonNewRoot;
            }

        }  catch (JSONException e) {}

        return null;
    }
}
