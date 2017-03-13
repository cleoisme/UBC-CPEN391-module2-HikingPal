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
 * Created by YueyueZhang on 2017-03-12.
 */

public class ViewHistoryFragment extends Fragment {

    public ViewHistoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.view_history_frag, container, false);
        return ll;
    }
}
