package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cpen391.module2.hikingpal.MainActivity;
import com.cpen391.module2.hikingpal.R;

import static com.cpen391.module2.hikingpal.MainActivity.buttonNum;
import static com.cpen391.module2.hikingpal.MainActivity.setButtonText;

/**
 * Created by YueyueZhang on 2017-03-12.
 */
public class NewTrailFragment extends Fragment {


    static MapViewFragment mapFragment;

    public NewTrailFragment() {
    }

    public static Button trailButton;
    public static Button finishButton;

    public static Spinner spinner;
    public static ArrayAdapter<CharSequence> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.new_trail_frag, container, false);

        trailButton = (Button) ll.findViewById(R.id.trail_Button);

        setButtonText(trailButton,buttonNum);

        MainActivity.trailButtonClick(trailButton);

        finishButton = (Button) ll.findViewById(R.id.finish_Button);
        MainActivity.finishButtonClick(finishButton);

        spinner = (Spinner) ll.findViewById(R.id.spinner1);
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.map_type, android.R.layout.simple_spinner_item);
        MainActivity.mapType_spinner();

        return ll;
    }
}