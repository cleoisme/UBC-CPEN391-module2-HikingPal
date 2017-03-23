package com.cpen391.module2.hikingpal.module;

/**
 * Created by YueyueZhang on 2017-03-13.
 */

public class MapImage {

    private int imageId; //put the path tp the json file
    private long myDuration;
    private long myDistance;
    private String[] mySpots;
    private String myDate;
    private int myRating;
    private String absPath;

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

    public String[] getMySpots() {
        return mySpots;
    }

    public String getAbsPath() {
        return absPath;
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

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }
}
