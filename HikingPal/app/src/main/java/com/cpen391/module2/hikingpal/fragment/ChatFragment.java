package com.cpen391.module2.hikingpal.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpen391.module2.hikingpal.R;

/**
 * Created by YueyueZhang on 2017-04-01.
 */
public class ChatFragment extends Fragment {

    public ChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.chat_layout, container, false);

        return ll;
    }
}
