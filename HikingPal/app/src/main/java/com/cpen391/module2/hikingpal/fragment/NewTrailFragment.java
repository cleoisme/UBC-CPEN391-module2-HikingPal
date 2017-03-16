package com.cpen391.module2.hikingpal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cpen391.module2.hikingpal.MainActivity;
import com.cpen391.module2.hikingpal.R;

/**
 * Created by YueyueZhang on 2017-03-12.
 */
public class NewTrailFragment extends Fragment {


    static MapViewFragment mapFragment;

    public NewTrailFragment() {
    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.new_trail_frag, container, false);

        Button startButton = (Button) ll.findViewById(R.id.track_start);
        Button stopButton = (Button) ll.findViewById(R.id.track_stop);
        Button rateButton = (Button) ll.findViewById(R.id.rate_track);
        MainActivity.StartButtonClick(startButton);
        MainActivity.StopButtonClick(stopButton);

        rateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).sendMessage("R");
            }
        });

        return ll;
    }
}
