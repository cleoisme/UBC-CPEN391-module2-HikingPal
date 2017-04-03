package com.cpen391.module2.hikingpal.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cpen391.module2.hikingpal.MainActivity;
import com.cpen391.module2.hikingpal.MapImageStorage;
import com.cpen391.module2.hikingpal.R;
import com.cpen391.module2.hikingpal.Utility.GetNearbyPlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cpen391.module2.hikingpal.MainActivity.buttonNum;
import static com.cpen391.module2.hikingpal.MainActivity.running;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.trailButton;

/**
 * Created by YueyueZhang on 2017-03-06.
 */

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    public static GoogleMap mMap;
    MapView mapView;


    public static long startTime;
    public static long stopTime;
    public static double totalDistance = 0;
    public static int zoomable = 0;

    private ArrayList<LatLng> points;
    Polyline line;

    public MapViewFragment() {

    }

    public static LatLng latlng;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public boolean initMap = true;

    public static List<Marker> markerList = new ArrayList<Marker>();
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

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setIndoorEnabled(true);
                //mMap.getUiSettings().setZoomControlsEnabled(true);

                //mMap.getUiSettings().setCompassEnabled(true);
                mMap.setPadding(0, 5, 0, 100);

//
//                latlng = new LatLng(49.260482, -123.253919);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {

                        if(initMap == true && zoomable==0) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
                            initMap = false;
                            zoomable = 1;
                        }
//                        else{
//                            mMap.animateCamera(CameraUpdateFactory.newLatLng(
//                                    new LatLng(location.getLatitude(), location.getLongitude())));
//                        }

                        latlng = new LatLng(location.getLatitude(), location.getLongitude());

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


    private int PROXIMITY_RADIUS = 1000;
    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCM6psxnSLbX5RzJJ874mrv5fkG0Ho4Jns");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public static int nearbyCase = 0;
    public void getNearby(int i){
        //mMap.clear();
        int height = 70;
        int width = 70;

        String nearBy = null;
        Bitmap nearbyPin;
        BitmapDrawable Pin = null;

        switch (i){
            case 1:
                nearBy = "food";
                Pin=(BitmapDrawable)getResources().getDrawable(R.drawable.food_pin);
                break;
            case 2:
                nearBy = "park";
                Pin=(BitmapDrawable)getResources().getDrawable(R.drawable.park_pin);
                break;
            case 3:
                nearBy = "gym";
                Pin=(BitmapDrawable)getResources().getDrawable(R.drawable.gym_pin);
                break;
            case 4:
                nearBy = "store";
                Pin=(BitmapDrawable)getResources().getDrawable(R.drawable.store_pin);
                break;
        }

        nearbyCase = i;
        Bitmap b=Pin.getBitmap();
        nearbyPin = Bitmap.createScaledBitmap(b, width, height, false);
        String url = getUrl(latlng.latitude, latlng.longitude, nearBy);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.getPin(nearbyPin);
        getNearbyPlacesData.execute(DataTransfer);
    }

    Marker startMarker;
    Marker stopMarker;
    public void startRecord() {

        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.start_pin);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap icon = Bitmap.createScaledBitmap(b, width, height, false);

        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.start_pin);

        Duration = 0;
        totalDistance = 0;
        distance = 0;

        startTime = System.currentTimeMillis();
            points.add(latlng);
            startMarker = mMap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title("start")
                            .icon(BitmapDescriptorFactory.fromBitmap(icon)));
    }

    double distance = 0;
    private void drawLine(){

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
            if(i>0){
                distance = distance + CalculationByDistance(points.get(i-1),points.get(i));
                Log.i("Total Distance: ", +distance + "meter");

            }
        }
        line = mMap.addPolyline(options);
    }

    public static long Duration;
    public void stopRecord(){
        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.stop_pin);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap icon = Bitmap.createScaledBitmap(b, width, height, false);

        // TODO: 2017-04-01 save the duration and distance
        long currentTime = System.currentTimeMillis();
        Duration = (currentTime - startTime) / 1000;
        totalDistance = distance;
        Log.i("Duration: ", + Duration + "s");
        Log.i("totalDistance: ", + totalDistance + "m");

        stopTime = System.currentTimeMillis();
        stopMarker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title("stop")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
        );
    }

    public void continueRecord(){
        stopMarker.remove();
    }

    long numPic;
    public void finishRecord(){
                    new AlertDialog.Builder(getActivity())
                    .setTitle("Exit")
                    .setMessage("Do you want to save the trail before exit?")

                    .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                    //running = false;
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mMap.clear();
                            points.clear();
                            zoomable = 0;
                            buttonNum = 1;
                            totalDistance=0;
                            running = false;
                            trailButton.setText("Start");
                        }
                    })
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //running = false;
                            // TODO: 2017-03-22 save the map
                            if(buttonNum==1){
                                Toast.makeText(getActivity(), "you have not start a trail yet!.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (buttonNum == 2) {
                                    stopRecord();
                                }
                                autoZoom();
                                final GoogleMap.SnapshotReadyCallback snapReadyCallback = new GoogleMap.SnapshotReadyCallback() {
                                    Bitmap bitmap;

                                    @Override
                                    public void onSnapshotReady(Bitmap snapshot) {
                                        bitmap = snapshot;
                                        try {
                                            final long myID = System.currentTimeMillis();
                                            final String myDate = (new Date(myID)).toString();
                                            final String myPath = "sdcard/hikingPal/saveTrail/" + myID + ".png";

                                            boolean result = savePic(bitmap, "sdcard/hikingPal/saveTrail/" + myID + ".png");

                                            //hardcoded
                                            final long myDuration = Duration;
                                            final long myDistance = (long) totalDistance;

                                            final List<String> mySpots = null;
                                            final MapImageStorage mis = new MapImageStorage(getActivity());
                                            final int subscribe = 0;
                                            //write to storage

                                            // TODO: 2017-03-28 test if we write it correctly, use the read operation/log.e

                                            if (result) {
                                                new AlertDialog.Builder(getActivity())
                                                        .setTitle("Successfully Saved!")
                                                        .setMessage("Do you want to rate the current track? ")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                ((MainActivity) getActivity()).sendMessage("P");
                                                                //TODO: 2017-04-03 update the rating for the current image before saving it


                                                            }
                                                        })
                                                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                int myRating = -1;
                                                                mis.writeToStorage((int) myID, subscribe, myDuration, myDistance, mySpots, myDate, myRating, myPath);

                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                Toast.makeText(getActivity(), "failed to save!.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        mMap.clear();
                                        points.clear();
                                        buttonNum = 1;
                                        initMap = true;
                                        zoomable = 0;
                                        totalDistance = 0;
                                        running = false;
                                        trailButton.setText("Start");

                                    }
                                };

                                GoogleMap.OnMapLoadedCallback mapLoadedCallback = new GoogleMap.OnMapLoadedCallback() {
                                    @Override
                                    public void onMapLoaded() {
                                        mMap.snapshot(snapReadyCallback);
                                    }
                                };
                                mMap.setOnMapLoadedCallback(mapLoadedCallback);
                            }
                            }

                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
    }


    private void autoZoom(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startMarker.getPosition());
        builder.include(stopMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }

    private static boolean savePic(Bitmap pBitmap,String strName)
    {
        FileOutputStream fos = null;
        try {
            fos=new FileOutputStream(strName);
            if(null!=fos) {
                pBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void maptypeButtonClick(int i){
        switch(i){
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

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
