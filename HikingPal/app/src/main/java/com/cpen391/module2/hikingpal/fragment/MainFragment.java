package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cpen391.module2.hikingpal.R;

/**
 * Created by YueyueZhang on 2017-03-14.
 */

public class MainFragment extends Fragment {
    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DrawerLayout dl = (DrawerLayout)inflater.inflate(R.layout.activity_main, container, false);
        return dl;
    }
}
