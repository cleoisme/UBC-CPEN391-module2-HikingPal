package com.cpen391.module2.hikingpal.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cpen391.module2.hikingpal.R;

import static android.R.attr.button;
import static android.R.attr.left;
import static android.R.attr.top;
import static android.content.ContentValues.TAG;
import static android.icu.util.IndianCalendar.IE;
import static android.view.Gravity.CENTER;

/**
 * Created by YueyueZhang on 2017-03-12.
 */
public class NewTrailFragment extends Fragment {

    public NewTrailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.new_trail_frag, container, false);
        return ll;
    }
}
