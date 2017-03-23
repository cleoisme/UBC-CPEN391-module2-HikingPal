/*
 * Copyright © 2017 · Android Tutorial Point
 */

package com.cpen391.module2.hikingpal.Nearby;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import static com.cpen391.module2.hikingpal.fragment.MapViewFragment.mMap;
import static com.cpen391.module2.hikingpal.fragment.MapViewFragment.markerList;
import static com.cpen391.module2.hikingpal.fragment.MapViewFragment.nearbyCase;

/**
 * Created by luvianwang on 2017-03-21.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        try {
            //Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            //Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            //Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    List<HashMap<String, String>> nearbyFoodList = null;
    List<HashMap<String, String>> nearbyParkList = null;
    List<HashMap<String, String>> nearbyGymList = null;
    List<HashMap<String, String>> nearbyStoreList = null;
    @Override
    protected void onPostExecute(String result) {

        //List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();

        if(nearbyCase==1){ //food
            nearbyFoodList =  dataParser.parse(result);
            ShowNearbyPlaces(nearbyFoodList);
        }
        else if(nearbyCase==2){ //park
            nearbyParkList =  dataParser.parse(result);
            ShowNearbyPlaces(nearbyParkList);

        }
        else if(nearbyCase==3){ //gym
            nearbyGymList =  dataParser.parse(result);
            ShowNearbyPlaces(nearbyGymList);
        }
        else if(nearbyCase==4){ //store
            nearbyStoreList =  dataParser.parse(result);
            ShowNearbyPlaces(nearbyStoreList);

        }
    }

    //List<MarkerOptions> nearbyMarkers = new ArrayList<MarkerOptions>();
    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        //Log.d("hhh %d", String.valueOf(lastIndex));
        clearPin();
            //Log.d("init %d", String.valueOf(lastIndex));
        int i;
        for (i = 0; i < nearbyPlacesList.size(); i++) {
            //Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            Marker marker;

            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            marker = mMap.addMarker(markerOptions);
            markerList.add(marker);
            //move map camera
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        Log.d("end %d", String.valueOf(i));
    }

    public static void clearPin(){
        for(int i=0;i<markerList.size();i++) {
            Marker marker = markerList.get(i);
            marker.remove();
            Log.d("delete %d", String.valueOf(i));
        }
        markerList.clear();
    }
    Bitmap icon;
    public void getPin(Bitmap nearbyPin){
         icon = nearbyPin;
    }


}