package com.cpen391.module2.hikingpal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.module2.hikingpal.Utility.GetNearbyPlacesData;
import com.cpen391.module2.hikingpal.Utility.WeatherHTTPClient;
import com.cpen391.module2.hikingpal.bluetooth.BluetoothChatService;
import com.cpen391.module2.hikingpal.bluetooth.Constants;
import com.cpen391.module2.hikingpal.bluetooth.DeviceListActivity;
import com.cpen391.module2.hikingpal.fragment.AnnouncementFragment;
import com.cpen391.module2.hikingpal.fragment.ChatFragment;
import com.cpen391.module2.hikingpal.fragment.DiscoverNearbyFragment;
import com.cpen391.module2.hikingpal.fragment.MapViewFragment;
import com.cpen391.module2.hikingpal.fragment.NewTrailFragment;
import com.cpen391.module2.hikingpal.fragment.ViewHistoryFragment;
import com.cpen391.module2.hikingpal.module.MapImage;
import com.cpen391.module2.hikingpal.module.Weather;
import com.cpen391.module2.hikingpal.parser.WeatherJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.cpen391.module2.hikingpal.R.id.center_horizontal;
import static com.cpen391.module2.hikingpal.R.id.fragment_container;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_large;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_large2;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_med1;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_small;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.adapter;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.spinner;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.trailButton;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_ALL_MAP_PERMISSIONS = 1;
    static MapViewFragment mapFragment;
    static NewTrailFragment newtrailFrag;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;

    private String mWeatherText = null;
    private String mWeatherIcon = null;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public static FloatingActionButton dfb;

    public static ViewHistoryFragment curFrag2;
    public static ChatFragment curFrag4;
    public static AnnouncementFragment curFrag5;

    private StringBuilder mBluetoothData = new StringBuilder();
    public static final char BLUETOOTH_RATE = 'P';
    public static final char BLUETOOTH_WEATHER = 'Z';
    public static final char BLUETOOTH_GPS = 'Y';

    private enum State{
        None,
        Rate,
        Weather,
        Map,
    };

    private State state = State.None;

    public static int buttonNum;
    public HikingPalStorage hikingPalStorage;

    View navigationView;
    public static ProgressBar waitIcon;
    public static View waiting_view;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SetupMapStorage", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File direct = new File(Environment.getExternalStorageDirectory()+"/hikingPal/saveTrail");

        if(!direct.exists()) {
            if(direct.mkdir());
            Log.d("hhh","folder created");
        }

        //setup the database
        hikingPalStorage = new HikingPalStorage(getApplicationContext());
        if((savedInstanceState != null ) && (savedInstanceState.getBoolean("SetupMapStorage") != true)){
            hikingPalStorage.setUp();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }


        //Inflate the container
        setContentView(R.layout.activity_main);

        navigationView = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);


        obtainPermissions();
        CheckGooglePlayServices();

        //hide the discover fab
        FloatingActionButton dfab = (FloatingActionButton) findViewById(R.id.discover_fab);
        dfab.hide();

        //setup the toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                if(curFrag4.isVisible()){
                    curFrag4.hindkb();
                }
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView textView = (TextView) findViewById(R.id.weather_info);
        if (textView != null && textView.getText() != null && textView.getText().length() != 0) {
            textView.setText(mWeatherText);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mapFragment = new MapViewFragment();
        ft.add(fragment_container, mapFragment, getResources().getString(R.string.map_view_tag));
        ft.commit();

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{"Vancouver"});

        //brings up the notification after dark
        notifier();
        app_start = true;
        newtrailFrag = new NewTrailFragment();
        dfb = (FloatingActionButton) findViewById(R.id.discover_fab);
        curFrag2 = new ViewHistoryFragment();
        curFrag4 = new ChatFragment();
        curFrag5 = new AnnouncementFragment();
        DF = new DiscoverNearbyFragment();

        //waiting_view = findViewById(R.id.container_waiting);
        //waiting_view.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        waiting_view = findViewById(R.id.container_waiting);
        waitIcon = (ProgressBar)findViewById(R.id.loading_spinner);
        //waitIcon.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        //add the popup checking
        while(true){
            if(image_ID != 0) {
                imagePopup(hikingPalStorage, image_ID);
            }else{
                break;
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    void obtainPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,

        };

        for (int i = 0; i < permissions.length; i++) {
            int hasFineLocation = ActivityCompat.checkSelfPermission(this, permissions[i]);
            if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, REQUEST_ALL_MAP_PERMISSIONS);
            }
        }
    }


    private void CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(DF.isVisible()){
            ft.remove(DF);
            if(mapFragment != null){
                GetNearbyPlacesData.clearPin();
                ft.show(newtrailFrag);
                setButtonText(trailButton,buttonNum);
                count++;
                getSupportActionBar().setTitle("New Trail");
            }
            me.getItem(1).setVisible(false);
            me.getItem(2).setVisible(false);
            me.getItem(3).setVisible(false);
            ft.commit();
        } else if (newtrailFrag.isVisible()) {
            ft.hide(newtrailFrag);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
            ft.commit();
            me.getItem(1).setVisible(false);
            me.getItem(2).setVisible(false);
            me.getItem(3).setVisible(false);
        }else if (curFrag2.isVisible()) {
            ft.hide(curFrag2);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            ft.commit();
            me.getItem(1).setVisible(false);
            me.getItem(2).setVisible(false);
            me.getItem(3).setVisible(false);
        }else if(curFrag4.isVisible()){
            ft.hide(curFrag4);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            ft.commit();
            me.getItem(1).setVisible(false);
            me.getItem(2).setVisible(false);
            me.getItem(3).setVisible(false);
        }
        else if(curFrag5.isVisible()){
            ft.hide(curFrag5);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            ft.commit();
            me.getItem(1).setVisible(false);
            me.getItem(2).setVisible(false);
            me.getItem(3).setVisible(false);
        }
        else {
            super.onBackPressed();
        }
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            WeatherHTTPClient weatherHTTPClient = new WeatherHTTPClient();
            Weather weather = new Weather();
            String data = (weatherHTTPClient.getWeatherData());

            try {
                weather = WeatherJSONParser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            TextView dateText = (TextView) navigationView.findViewById(R.id.date_field);
            if(dateText != null) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy");
                String dateString = formatter.format(date);
                dateText.setText(dateString);
            }

            ImageView imageView =  (ImageView) navigationView.findViewById(R.id.weather_icon);
            if(imageView != null && weather.currentCondition.getIcon() != null) {
                imageView.setImageResource(getWeatherIcons().get(weather.currentCondition.getIcon()));
            }


            TextView textView = (TextView) navigationView.findViewById(R.id.weather_info);
            mWeatherText = weather.currentCondition.getDescr().substring(0, 1).toUpperCase() + weather.currentCondition.getDescr().substring(1) + "\nTemp: " + weather.temperature.getTemp() + " degree Celsius";
            mWeatherIcon = weather.currentCondition.getIcon();
            if (textView != null) {
                textView.setText(mWeatherText);
                return;
            }
        }
    }

    Menu me;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        me = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.trail_settings:
                //Toast.makeText(MainActivity.this, "nnn", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Send all Trails?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                curFrag2.sendAllTrails();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                return true;
            case R.id.chat_settings:
                //Toast.makeText(MainActivity.this, "hhh", Toast.LENGTH_SHORT).show();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete all conversations?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                curFrag4.delAllConv();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            case R.id.ann_settings:
                Toast.makeText(MainActivity.this, "lalala", Toast.LENGTH_SHORT).show();
                // TODO: 2017-04-04 @cleo 
                return true;
            case R.id.secure_connect_scan:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void waiting(){
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                waiting_view.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                waiting_view.setVisibility(View.INVISIBLE);
            }

        }.start();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        MyFragmentManager(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if(curFrag4.isVisible()) {
            curFrag4.hindkb();
        }
        return true;
    }

    boolean app_start;

    private void SetupFragment(){
        if(app_start) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(fragment_container_small, newtrailFrag, getResources().getString(R.string.new_trail_tag));
            ft.add(fragment_container_med1, curFrag2, getResources().getString(R.string.view_history_tag));
            ft.add(fragment_container_large2, curFrag4, getResources().getString(R.string.group_chat));
            ft.add(fragment_container_large, curFrag5, getResources().getString(R.string.announcement));
            buttonNum=1;
            count = 1;
            app_start = false;
        }
    }

    private void HandleViewHistory(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        getSupportActionBar().setTitle(getResources().getString(R.string.view_history_tag));
        dfb.hide();
        ft.hide(newtrailFrag);
        ft.hide(curFrag4);
        ft.hide(curFrag5);
        ft.show(curFrag2);
    }

    public void MyFragmentManager(int fragmentID) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //todo: PartialAnnouncement frag need to be added
        if(app_start == true) {
            ft.add(fragment_container_small, newtrailFrag, getResources().getString(R.string.new_trail_tag));
            ft.add(fragment_container_med1, curFrag2, getResources().getString(R.string.view_history_tag));
            ft.add(fragment_container_large2, curFrag4, getResources().getString(R.string.group_chat));
            ft.add(fragment_container_large, curFrag5, getResources().getString(R.string.announcement));
            buttonNum=1;
            count = 1;
            app_start = false;
        }


        switch (fragmentID) {
            case R.id.new_trail:
                me.getItem(1).setVisible(false);
                me.getItem(2).setVisible(false);
                me.getItem(3).setVisible(false);
                
                ft.hide(curFrag2);
                ft.hide(curFrag4);
                ft.hide(curFrag5);
                ft.show(newtrailFrag);
                getSupportActionBar().setTitle(getResources().getString(R.string.new_trail_tag));
                DiscoverFabOnClick(dfb, mapFragment);
                break;

            case R.id.view_history:
                me.getItem(1).setVisible(true);
                me.getItem(2).setVisible(false);
                me.getItem(3).setVisible(false);

                getSupportActionBar().setTitle(getResources().getString(R.string.view_history_tag));
                dfb.hide();
                ft.hide(newtrailFrag);
                ft.hide(curFrag4);
                ft.hide(curFrag5);
                ft.show(curFrag2);
                ft.remove(DF);
                GetNearbyPlacesData.clearPin();
                count =1;
                //ft.addToBackStack(null);
                break;

            case R.id.chat:
                me.getItem(1).setVisible(false);
                me.getItem(2).setVisible(true);
                me.getItem(3).setVisible(false);
                dfb.hide();
                ft.hide(newtrailFrag);
                ft.hide(curFrag2);
                ft.hide(curFrag5);
                ft.show(curFrag4);
                ft.remove(DF);
                GetNearbyPlacesData.clearPin();
                count =1;
                getSupportActionBar().setTitle("Chat");
                //ft.addToBackStack(null);
                break;

            case R.id.announcement:
                me.getItem(1).setVisible(false);
                me.getItem(2).setVisible(false);
                me.getItem(3).setVisible(true);
                ft.hide(newtrailFrag);
                ft.hide(curFrag2);
                ft.hide(curFrag4);
                ft.show(curFrag5);
                ft.remove(DF);
                GetNearbyPlacesData.clearPin();
                count =1;
                dfb.hide();
                getSupportActionBar().setTitle("Announcement");
                break;

            default:
                break;
        }
        ft.commit();
    }

    public static void getNearby(final ImageButton nearbyButton, final int i){
        nearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DF.tap(i);
                mapFragment.getNearby(i);
            }
        });
    }


    public static boolean running = false;

    public static void setButtonText(Button trailButton, int i){
        if(i==1) {
            trailButton.setText("start");
        }
        else if(i==2){
            trailButton.setText("stop");
        }
        else if(i==3){
            trailButton.setText("resume");
        }
    }
    public static void trailButtonClick(final Button trailButton){
        trailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(buttonNum==1){ //init
                    buttonNum=2;
                    setButtonText(trailButton,buttonNum);
                    //trailButton.setText("Stop");
                    running = true;
                    mapFragment.startRecord();
                }
                else if(buttonNum==2){ //stop
                    buttonNum=3;
                    setButtonText(trailButton,buttonNum);
                    //trailButton.setText("Resume");
                    running = false;
                    mapFragment.stopRecord();
                }
                else if(buttonNum==3){ //resume
                    buttonNum=2;
                    setButtonText(trailButton,buttonNum);
                    //trailButton.setText("Stop");
                    running = true;
                    mapFragment.continueRecord();
                }
            }
        });
    }

    public static void finishButtonClick(final Button finishButton) {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.finishRecord();
            }
        });
    }


    public static void mapType_spinner(){
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) getActivity());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mapFragment.maptypeButtonClick(position);
                //Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mapFragment.maptypeButtonClick(0);
            }
        });
    }

    public static int count;
    static DiscoverNearbyFragment DF;
    public void DiscoverFabOnClick(FloatingActionButton dfb, final MapViewFragment mv) {
        dfb.show();
        dfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                //fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();


                if(count%2!=0){
                    getSupportActionBar().setTitle(getResources().getString(R.string.discover_nearby_tag));
                    ft.add(R.id.fragment_container_long, DF, getResources().getString(R.string.discover_nearby_tag));
                    //ft.replace(R.id.fragment_container_long,DF);
                    ft.hide(newtrailFrag);
                    //ft.addToBackStack(null);
                    ft.commit();
                }
                else{
                    getSupportActionBar().setTitle(getResources().getString(R.string.new_trail_tag));
                    ft.remove(DF);
                    GetNearbyPlacesData.clearPin();
                    ft.show(newtrailFrag);
                    //ft.addToBackStack(null);
                    ft.commit();
                }
                count++;
            }
        });
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private String GetGpsString(){
        String lat = String.format("%.3f", Math.abs(MapViewFragment.latlng.latitude));
        String lon = String.format("%.3f", Math.abs(MapViewFragment.latlng.longitude));
        if(MapViewFragment.latlng.latitude < 0){
            lat += "S";
        }
        else{
            lat += "N";
        }
        if(MapViewFragment.latlng.longitude < 0){
            lon += "W";
        }
        else{
            lon += "E";
        }
        return lat + " " + lon;
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            MainActivity.this.sendMessageSlow(BLUETOOTH_GPS + GetGpsString() + BLUETOOTH_GPS);
                            MainActivity.this.sendMessageSlow(BLUETOOTH_WEATHER + mWeatherIcon + mWeatherText + BLUETOOTH_WEATHER);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    //Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_SHORT).show();
                    if(readMessage.charAt(0) == 'Y'){
                        // todo
                    }

                    if(state == State.None && mBluetoothData.length() == 0){
                        if(readMessage.charAt(0) == BLUETOOTH_RATE) {
                            state = State.Rate;
                        }
                    }
                    else {
                        if(state == State.Rate && readMessage.charAt(0) == BLUETOOTH_RATE) {
                            int stars = Integer.parseInt(mBluetoothData.toString());
                            mapFragment.rating = stars;
                            mapFragment.saveToStorage();
                            Toast.makeText(getBaseContext(), stars + " Stars!", Toast.LENGTH_SHORT).show();
                            mBluetoothData.setLength(0);
                            state = State.None;
                        }
                        else{
                            mBluetoothData.append(readMessage);
                        }
                    }

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getBaseContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getBaseContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getBaseContext(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getBaseContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    public void sendMessageSlow(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            return;
        }
        for (int i = 0; i < message.length(); ++i) {
            Character c = message.charAt(i);
            sendMessage(c.toString());
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check current time and show up the notification if necessary
     */
    private void notifier() {
        Calendar c = Calendar.getInstance();
        //get current month: 0~11 -> Jan~Dec
        int month = c.get(Calendar.MONTH);
        //get current hour
        int hour = c.get(Calendar.HOUR_OF_DAY);
        //standard time
        if (month >= 10 || month < 2) {
            //after 21:00 pm
            if (hour >= 21) {
                new AlertDialog.Builder(this)
                        .setTitle("It is getting dark be careful")
                        .setMessage("Call your group mate if you are walking alone")
                        .show();
            }
        }
    }


    private void imagePopup(HikingPalStorage hps, long id){
        final FrameLayout fl = (FrameLayout) findViewById(R.id.popup_view);
        final LinearLayout ll = (LinearLayout) fl.getChildAt(0);

        Button bt = new Button(this);
        bt.setText("Close");
        bt.setWidth(30);
        bt.setHeight(10);
        bt.setGravity(center_horizontal);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fl.removeAllViewsInLayout();
            }
        });

        List<MapImage> myList = hps.getAllMapImages();

        for(MapImage mapImage : myList){
            if(mapImage.getImageId() == id){
                Bitmap image = BitmapFactory.decodeFile(mapImage.getAbsPath());
                ImageView iv = new ImageView(this);
                iv.setImageBitmap(image);
                iv.setScaleType(ImageView.ScaleType.FIT_START);
                ll.addView(iv);
                ll.addView(bt);

            }else{
                break;
            }
        }

    }

    private HashMap<String, Integer> getWeatherIcons() {

        HashMap<String, Integer> weatherIcons = new HashMap<String, Integer>();

        weatherIcons.put("01d", R.drawable.clearskyday);
        weatherIcons.put("01n", R.drawable.clearskynight);
        weatherIcons.put("02d", R.drawable.fewcloudsday);
        weatherIcons.put("02n", R.drawable.fewcloudsnight);
        weatherIcons.put("03d", R.drawable.scatteredcloudsday);
        weatherIcons.put("03n", R.drawable.scatteredcloudsnight);
        weatherIcons.put("04d", R.drawable.brokencloudsday);
        weatherIcons.put("04n", R.drawable.brokencloudsnight);
        weatherIcons.put("09d", R.drawable.showerrainday);
        weatherIcons.put("09n", R.drawable.showerrainnight);
        weatherIcons.put("10d", R.drawable.rainday);
        weatherIcons.put("10n", R.drawable.rainnight);
        weatherIcons.put("11d", R.drawable.thunderstormday);
        weatherIcons.put("11n", R.drawable.thunderstormnight);
        weatherIcons.put("13d", R.drawable.snowday);
        weatherIcons.put("13n", R.drawable.snownight);
        weatherIcons.put("50d", R.drawable.mistday);
        weatherIcons.put("50n", R.drawable.mistnight);

        return weatherIcons;
    }
}