package com.cpen391.module2.hikingpal;

import android.content.Context;
import android.content.SharedPreferences;

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

public class MapImageStorage {

    FileOutputStream fos;
    DataOutputStream dos;

    String mapImage = "MapImages";

    Context context;

    private final static String storage = "storage.json";

    private SharedPreferences settings;

    public MapImageStorage(Context context){
        this.context = context;
        this.settings = context.getSharedPreferences("prefs", 0);
    }

    public List<String> getMapImageList() {
        String[] mapImageArray = {mapImage};
        return new ArrayList<String>(Arrays.asList(mapImageArray));
    }


    public void setUp() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE));
            write(outputStreamWriter, makeRoot().toString());
        } catch (FileNotFoundException e) {}
    }


    private JSONObject makeRoot() {
        JSONObject root =  new JSONObject();
        List<String> mapImageList = getMapImageList();
        try{
            for(int i = 0; i < mapImageList.size(); i++){
                root.put(mapImageList.get(i), new JSONArray());
            }
        } catch (JSONException e) {}

        return root;
    }

    public void writeToStorage(long imageId, long myDuration, long myDistance, List<String> mySpots, String myDate, int myRating, String pathToImage){

        JSONObject element = create(imageId, myDuration, myDistance, mySpots, myDate, myRating, pathToImage);

        String jsonRoot = readFile();

        try {
            write(new OutputStreamWriter(context.openFileOutput(storage, Context.MODE_PRIVATE)), add_to_root(element, jsonRoot, mapImage));
        } catch (FileNotFoundException e) {}

    }

    private JSONObject create(long imageId, long myDuration, long myDistance, List<String> mySpots, String myDate, int myRating, String pathToImage) {
        JSONObject element = new JSONObject();
        try {
            element.put("imageId", imageId);
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


    private String readFile(){
        InputStreamReader isr;
        try{
            isr = new InputStreamReader(context.openFileInput(storage));
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

    private String add_to_root(JSONObject element, String root, String mapImageType) {
        assert(element != null);
        try {
            JSONObject jsonRootObject;
            JSONArray jsonArray;
            // If root object exists
            if (root != "") {
                // Make a JSON Object from root String
                jsonRootObject = new JSONObject(root);
                // Get the JSON Array containing "Foods"
                jsonArray = jsonRootObject.optJSONArray(mapImageType);
                if (jsonArray != null) {
                    // Append the new element
                    jsonArray.put(element);
                }
                else {
                    jsonArray = new JSONArray();
                    // Put the new element into the array
                    jsonArray.put(element);
                    // Add the JSON Array to JSON Root Object
                    jsonRootObject.put(mapImageType, jsonArray);
                }
            }
            // If root does not exist
            else {
                // Make a new JSON Root Object
                jsonRootObject = new JSONObject();
                // Make a new JSON Array
                jsonArray = new JSONArray();
                // Put the new element into the array
                jsonArray.put(element);
                // Add the JSON Array to JSON Root Object
                jsonRootObject.put(mapImageType, jsonArray);
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
        String root = readFile();
        return extractArray(root, restaurant);
    }

    private JSONArray extractArray(String root, String attribute) {
        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(attribute);

            return jsonArray;
        } catch (JSONException e) {}

        return null;
    }

    public JSONObject getObject(long mapImageId){

        String root = readFile();
        return extractObject(root, mapImageId);
    }

    private JSONObject extractObject(String root, long mapImageId) {

        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(mapImage);

            int i = 0;
            for(i = 0; i < jsonArray.length(); i++){
                JSONObject element = jsonArray.getJSONObject(i);
                if(element.optInt("imageId") == mapImageId){
                    return element;
                }
            }
        } catch (JSONException e) {}

        return null;
    }

    public JSONObject getObject(String mapPath) {

        String root = readFile();
        return extractObject(root, mapPath);
    }

    private JSONObject extractObject(String root, String mapImageId) {

        try {
            JSONObject  jsonRootObject = new JSONObject(root);

            // Get the food array from root object
            JSONArray jsonArray = jsonRootObject.optJSONArray(mapImage);

            int i = 0;
            for(i = 0; i < jsonArray.length(); i++){
                JSONObject element = jsonArray.getJSONObject(i);
                if(element.optString("pathToImage").equals(mapImageId)){
                    return element;
                }
            }
        } catch (JSONException e) {}

        return null;
    }


}
