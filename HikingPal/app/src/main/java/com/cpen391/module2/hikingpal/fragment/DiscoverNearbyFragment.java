package com.cpen391.module2.hikingpal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpen391.module2.hikingpal.MainActivity;
import com.cpen391.module2.hikingpal.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static com.cpen391.module2.hikingpal.fragment.MapViewFragment.mMap;

/**
 * Created by YueyueZhang on 2017-03-13.
 */

public class DiscoverNearbyFragment extends Fragment {

    public DiscoverNearbyFragment() {
    }

    public static ImageButton btnRestaurant;
    public static ImageButton btnPark;
    public static ImageButton btnGym;
    public static ImageButton btnStore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.discover_nearby_frag, container, false);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);

                TextView info= (TextView) v.findViewById(R.id.info);

                info.setText(marker.getSnippet());

                return v;
            }
        });

        btnRestaurant = (ImageButton) ll.findViewById(R.id.btnRestaurant);
        MainActivity.getNearby(btnRestaurant,1);
        //didTapButton(container);
        btnPark = (ImageButton) ll.findViewById(R.id.btnPark);
        MainActivity.getNearby(btnPark,2);
        btnGym = (ImageButton) ll.findViewById(R.id.btnGym);
        MainActivity.getNearby(btnGym,3);
        btnStore = (ImageButton) ll.findViewById(R.id.btnStore);
        MainActivity.getNearby(btnStore,4);
        didTapButton(container);

        return ll;
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        btnRestaurant.startAnimation(myAnim);
        btnPark.startAnimation(myAnim);
        btnGym.startAnimation(myAnim);
        btnStore.startAnimation(myAnim);
    }

    public void tap(int i){
        final Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.4, 15);
        myAnim.setInterpolator(interpolator);
        if(i==1){
            btnRestaurant.startAnimation(myAnim);
        }
        else if(i==2){
            btnPark.startAnimation(myAnim);

        }
        else if(i==3){
            btnGym.startAnimation(myAnim);
        }
        else if(i==4){
            btnStore.startAnimation(myAnim);

        }
    }

    private class MyBounceInterpolator implements android.view.animation.Interpolator {
        double mAmplitude = 1;
        double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
}
