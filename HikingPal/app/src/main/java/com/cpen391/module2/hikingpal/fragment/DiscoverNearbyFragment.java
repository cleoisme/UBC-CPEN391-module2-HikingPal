package com.cpen391.module2.hikingpal.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    public static Button saveNbButton;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.discover_nearby_frag, container, false);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog dialog;
                Toast.makeText(getActivity(), "clicked!.", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);
                builder.setView(v);

                ImageView img = (ImageView) v.findViewById(R.id.image);
                img.setImageURI(Uri.parse(marker.getSnippet()));

                Log.d("URL: %s", String.valueOf(Uri.parse(marker.getSnippet())));
//                TextView info= (TextView) v.findViewById(R.id.info);
//                info.setText(marker.getSnippet());
//                builder.setTitle("My Dialog");
//                builder.setMessage("clicked!");
                dialog = builder.create();
                dialog.show();

                return true;
            }
        });

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            @Override
//            public View getInfoWindow(Marker arg0) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//
//                View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);
//
//                saveNbButton = (Button) v.findViewById(R.id.saveNbButton);
//                TextView info= (TextView) v.findViewById(R.id.info);
//                //TextView title= (TextView) v.findViewById(R.id.title);
//
//                //title.setText(marker.getTitle());
//                info.setText(marker.getSnippet());
//
//                saveNbButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "saved!.", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                });
//
//                return v;
//            }
//        });

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
