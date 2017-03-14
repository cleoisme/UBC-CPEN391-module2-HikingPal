package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cpen391.module2.hikingpal.R;

/**
 * Created by YueyueZhang on 2017-03-13.
 */

public class DiscoverNearbyFragment extends Fragment {

    public DiscoverNearbyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.discover_nearby_frag, container, false);
        return ll;
    }
}
