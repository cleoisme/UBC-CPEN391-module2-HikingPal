package com.cpen391.module2.hikingpal.fragment;

import android.support.v4.app.Fragment;

import com.cpen391.module2.hikingpal.module.Announcement;

/**
 * Created by YueyueZhang on 2017-04-04.
 */
public class SingleAnnounceFrag extends Fragment{

    //declare properties
    private Announcement announcement;
    private int prevFrag;

    public SingleAnnounceFrag() {
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public int getPrevFrag() {
        return prevFrag;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public void setPrevFrag(int prevFrag) {
        this.prevFrag = prevFrag;
    }
}
