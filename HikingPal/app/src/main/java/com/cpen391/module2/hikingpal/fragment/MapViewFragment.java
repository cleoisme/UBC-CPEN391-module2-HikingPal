package com.cpen391.module2.hikingpal.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cpen391.module2.hikingpal.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static com.cpen391.module2.hikingpal.MainActivity.buttonNum;
import static com.cpen391.module2.hikingpal.MainActivity.running;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.trailButton;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

/**
 * Created by YueyueZhang on 2017-03-06.
 */

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    GoogleMap mMap;
    MapView mapView;


    public static long startTime;
    public static long stopTime;
    public static double totalDistance = 0;

    private ArrayList<LatLng> points;
    Polyline line;

    public MapViewFragment() {

    }

    LatLng latlng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_map_view, container, false);
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        points = new ArrayList<LatLng>();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setMyLocationEnabled(true);
                mMap.setIndoorEnabled(true);

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {

                        //speed = (int) ((location.getSpeed() * 3600) / 1000);


                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(), location.getLongitude()), 18));

                        latlng = new LatLng(location.getLatitude(), location.getLongitude());

                        //if(StartIsPressed==true || ContinueIsPressed==true) {
                        if(running==true){
                            points.add(latlng);
                            drawLine();
                        }

                    }
                });
            }

        });


        return view;
    }


    @Override
    public void onConnected(Bundle connectionHint) {


    }


    Marker startMarker;
    Marker stopMarker;
    public void startRecord() {
        startTime = System.currentTimeMillis();
            points.add(latlng);
            startMarker = mMap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title("start")
                            .icon(BitmapDescriptorFactory.defaultMarker()));
    }

    private void drawLine(){

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
            if(i>0){
                totalDistance = totalDistance + CalculationByDistance(points.get(i-1),points.get(i));
                Log.i("Total Distance: ", +totalDistance + "meter");

//                long currentTime = System.currentTimeMillis();
//                long seconds = (currentTime - startTime) / 1000;
//                Log.i("Duration: ", + seconds + "s");

            }
        }
        line = mMap.addPolyline(options);
    }


    public void stopRecord(){

        stopTime = System.currentTimeMillis();
        stopMarker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title("stop")
                .icon(BitmapDescriptorFactory.defaultMarker(HUE_YELLOW))
        );
    }

    public void continueRecord(){
        stopMarker.remove();
    }

    public void finishRecord(){
                    new AlertDialog.Builder(getActivity())
                    .setTitle("finish")
                    .setMessage("finish?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mMap.clear();
                            points.clear();
                            buttonNum = 1;
                            totalDistance=0;
                            running = false;
                            trailButton.setText("Start");
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //running = false;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
    }



    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        double meter=valueResult%1000;
        return meter;
    }



    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
