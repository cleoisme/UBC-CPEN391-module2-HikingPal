package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
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

import java.util.Date;
import java.util.List;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.cpen391.module2.hikingpal.MainActivity.waiting_view;
import static com.cpen391.module2.hikingpal.R.dimen.button_margin;

/**
 * Created by YueyueZhang on 2017-04-01.
 */
public class AnnouncementFragment extends Fragment{
    private static final int LISTALLANNOUNCEMENT = 1;
    HikingPalStorage hikingPalStorage;
    List<Announcement> myList;

    public AnnouncementFragment() {
    }

    public static SingleAnnounceFrag s_fragment = new SingleAnnounceFrag();

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

        // TODO: 2017-04-04  All announcement sent from touchscreen need to be written to database first
        myList = hps.getAllAnnoucements();

        s_fragment = new SingleAnnounceFrag();

        if (myList == null){
            //for testing purpose
            final Announcement announcement = new Announcement();
            announcement.setTitle("Test Title");
            announcement.setId(System.currentTimeMillis());
            announcement.setContent("I am detailed information.");

            final Button bt = createButton(announcement);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s_fragment.setAnnouncement(announcement);
                    s_fragment.setPrevFrag(LISTALLANNOUNCEMENT);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById(R.id.fragment_container_large);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    //hide the current frag
                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_left);
                    transaction.remove(currentFrag);
                    transaction.add(R.id.fragment_container_large, s_fragment, String.valueOf(bt.getId()));

                    //reopen the all announcement frag
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            ll.addView(bt, lp);
        }
        else{
            //iterate the announcement list to create button for each announcement
            for (final Announcement announcement : myList) {
                //create button
                final Button button = createButton(announcement);
                //add the button to the layout
                ll.addView(button, lp);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Fragment currentFrag = fm.findFragmentById(R.id.fragment_container_large);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        s_fragment.setAnnouncement(announcement);
                        s_fragment.setPrevFrag(LISTALLANNOUNCEMENT);

                        //hide the current frag
                        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_left);
                        transaction.remove(currentFrag);
                        transaction.add(R.id.fragment_container_large, s_fragment, String.valueOf(button.getId()));

                        //reopen the all announcement frag
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        }
        return fl;
    }

    private Button createButton(Announcement announcement) {
        //set button properties
        Button bt = new Button(this.getActivity());

        bt.setGravity(Gravity.LEFT);
        bt.setAllCaps(false);
        bt.setPadding(getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin));
        bt.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_press_colors));
        bt.setTextColor(ContextCompat.getColor(getContext(),R.color.primaryTextColor));

        SpannableString buttonText = new SpannableString(announcement.getTitle() + "\n"
                                    + new Date(announcement.getId()) + "\n");

        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new RelativeSizeSpan(2), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new RelativeSizeSpan((float) 1.5), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        bt.setText(buttonText);
        return bt;
    }

    public void delAllAnn() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                waiting_view.setVisibility(View.VISIBLE);
                hikingPalStorage.removeAllAnnouncement();
            }
            public void onFinish() {
                waiting_view.setVisibility(View.INVISIBLE);
                myList.clear();
            }

        }.start();
    }
}
