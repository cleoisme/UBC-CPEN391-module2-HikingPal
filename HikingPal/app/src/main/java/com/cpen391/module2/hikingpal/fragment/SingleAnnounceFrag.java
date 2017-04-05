package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpen391.module2.hikingpal.R;
import com.cpen391.module2.hikingpal.module.Announcement;

import java.util.Date;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.single_announce, container, false);
        final int counter = ll.getChildCount();
        Log.i("child # = ?", String.valueOf(counter));
        
        setTitle(ll);
        setTime(ll);
        setContent(ll);
        
        return ll;
    }

    private void setTitle(LinearLayout ll) {
        SpannableString title = new SpannableString(announcement.getTitle());
        TextView tv3 = (TextView)ll.getChildAt(0);
        tv3.setText(title);
    }

    private void setTime(LinearLayout ll) {
        SpannableString time = new SpannableString((new Date(announcement.getId())).toString());
        TextView tv2 = (TextView)ll.getChildAt(1);
        tv2.setText(time);
    }

    private void setContent(LinearLayout ll) {
        SpannableString content = new SpannableString(announcement.getContent());
        TextView tv = (TextView)ll.getChildAt(2);
        tv.setText(content);
    }
}
