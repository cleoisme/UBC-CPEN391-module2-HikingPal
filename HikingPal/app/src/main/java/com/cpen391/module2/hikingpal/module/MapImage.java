package com.cpen391.module2.hikingpal.module;

import android.text.TextUtils;

/**
 * Created by YueyueZhang on 2017-03-13.
 */

public class MapImage {

    private int imageId; //put the path tp the json file
    private long myDuration;
    private long myDistance;
    private String[] mySpots;
    private String myDate;
    private String myName;
    private int myRating;

    private final char BT_INIT = 'Q';
    private final char BT_DELIMITER = 'V';

    public MapImage() {
    }

    public int getImageId() {
        return imageId;
    }

    public long getMyDistance() {
        return myDistance;
    }

    public long getMyDuration() {
        return myDuration;
    }

    public int getMyRating() {
        return myRating;
    }

    public String getMyDate() {
        return myDate;
    }

    public String getMyName(){ return myName; }

    public String[] getMySpots() {
        return mySpots;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setMyDistance(long myDistance) {
        this.myDistance = myDistance;
    }

    public void setMyDuration(long myDuration) {
        this.myDuration = myDuration;
    }

    public void setMyRating(int myRating) {
        this.myRating = myRating;
    }

    public void setMyDate(String myDate) {
        this.myDate = myDate;
    }

    public void setMySpots(String[] mySpots) {
        this.mySpots = mySpots;
    }

    // Parse all the data to send to bluetooth into a string
    public String GetDataString(){
        StringBuilder sb = new StringBuilder();
        sb.append(BT_INIT);
        sb.append(myName);
        sb.append(BT_DELIMITER);
        sb.append(myRating);
        sb.append(BT_DELIMITER);
        sb.append(myDistance);
        sb.append(BT_DELIMITER);
        sb.append(myDuration);
        sb.append(BT_DELIMITER);
        sb.append(TextUtils.join(",", mySpots));
        sb.append(BT_DELIMITER);
        sb.append(myDate);

        return sb.toString();
    }
}
