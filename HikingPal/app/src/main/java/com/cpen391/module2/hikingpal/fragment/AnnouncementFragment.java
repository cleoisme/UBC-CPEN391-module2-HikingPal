package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.cpen391.module2.hikingpal.HikingPalStorage;
import com.cpen391.module2.hikingpal.R;
import com.cpen391.module2.hikingpal.module.Announcement;

import java.util.List;

/**
 * Created by YueyueZhang on 2017-04-01.
 */
public class AnnouncementFragment extends Fragment{

    public AnnouncementFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //call database
        HikingPalStorage hps = new HikingPalStorage(getContext());

        //inflate the layout
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.announcement_history, container, false);
        ScrollView sv = (ScrollView) fl.getChildAt(0);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        //get the LinearLayout and layoutParams for new buttons
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //get all announcement from the database, new to old here
        List<Announcement> myList = hps.getAllAnnoucements();

        //iterate the announcement list to create button for each announcement
        for(final Announcement announcement : myList){
            //create button
            final Button button = createButton(announcement);
            //add the button to the layout
            ll.addView(button, lp);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SingleAnnounceFrag fragment = new SingleAnnounceFrag();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    fragment.setAnnouncement(announcement);
                    fragment.setPrevFragment(LISTALLANNOUNCEMENT);

                    //hide the current frag
                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_left);
                    transaction.remove(currentFrag);
                    transaction.add();

                    //reopen the all announcement frag
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        return fl;
    }

    private Button createButton(Announcement announcement) {
    }
}
