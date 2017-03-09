package com.cpen391.module2.hikingpal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



/**
 * Created by YueyueZhang on 2017-03-06.
 */

public class MapViewFragment extends Fragment {

    GoogleMap map;
    MapView mapView;

    public MapViewFragment(){

    }

//        @Override
//        public void onCreate (Bundle savedInstanceState){
//            super.onCreate(savedInstanceState);
//        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.activity_maps, container, false);
        //Creates and initializes the map view
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(-34, 151);
                map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        });

        return view;
    }
}
