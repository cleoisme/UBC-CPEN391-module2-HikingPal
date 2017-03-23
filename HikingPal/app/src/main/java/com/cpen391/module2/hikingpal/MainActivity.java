package com.cpen391.module2.hikingpal;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.module2.hikingpal.Nearby.GetNearbyPlacesData;
import com.cpen391.module2.hikingpal.bluetooth.BluetoothChatService;
import com.cpen391.module2.hikingpal.bluetooth.Constants;
import com.cpen391.module2.hikingpal.bluetooth.DeviceListActivity;
import com.cpen391.module2.hikingpal.fragment.DiscoverNearbyFragment;
import com.cpen391.module2.hikingpal.fragment.FavTrailsFragment;
import com.cpen391.module2.hikingpal.fragment.MapViewFragment;
import com.cpen391.module2.hikingpal.fragment.NewTrailFragment;
import com.cpen391.module2.hikingpal.fragment.ViewHistoryFragment;
import com.cpen391.module2.hikingpal.module.Weather;
import com.cpen391.module2.hikingpal.parser.WeatherJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;

import java.util.Calendar;

import static com.cpen391.module2.hikingpal.R.id.fragment_container;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_med1;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_med2;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_small;
import static com.cpen391.module2.hikingpal.fragment.MapViewFragment.mMap;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.adapter;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.spinner;
import static com.cpen391.module2.hikingpal.fragment.NewTrailFragment.trailButton;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_ALL_MAP_PERMISSIONS = 1;
    static MapViewFragment mapFragment;
    NewTrailFragment newtrailFrag = new NewTrailFragment();

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;

    private String mWeatherText = null;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public static int buttonNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }


        //Inflate the container
        setContentView(R.layout.activity_main);

        obtainPermissions();
        CheckGooglePlayServices();

        //hide the discover fab
        FloatingActionButton dfab = (FloatingActionButton) findViewById(R.id.discover_fab);
        dfab.hide();

        //setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        //ft.addToBackStack(null);
        ft.commit();

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{"Vancouver"});

        //brings up the notification after dark
        Notifier();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
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

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    void obtainPermissions() {

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
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

        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout fcl = (FrameLayout) findViewById(R.id.fragment_container_long);
        FrameLayout fcs = (FrameLayout) findViewById(fragment_container_small);
        FrameLayout fcm1 = (FrameLayout) findViewById(fragment_container_med1);
        FrameLayout fcm2 = (FrameLayout) findViewById(fragment_container_med2);
        FloatingActionButton dfb = (FloatingActionButton) findViewById(R.id.discover_fab);
        //setButtonText(trailButton,buttonNum);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d("hhh:","1");
        }else if(fcl.isDirty()){
            fcl.removeAllViewsInLayout();
            if(mapFragment != null){
                GetNearbyPlacesData.clearPin();
                ft.add(R.id.fragment_container_small, newtrailFrag, getResources().getString(R.string.new_trail_tag));
                setButtonText(trailButton,buttonNum);
                Log.d("buttonNum: %d", String.valueOf(buttonNum));
                count++;
            }
            ft.addToBackStack(null);
            ft.commit();
        } else if (fcs.isDirty()) {
            fcs.removeAllViewsInLayout();
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
            // TODO: 2017-03-22 need to warn user to save before clear the map
            mMap.clear();
            Log.d("hhh:","2");
        }else if(fcm1.isDirty()){
            fcm1.removeAllViewsInLayout();
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
            Log.d("hhh:","3");
        }else if(fcm2.isDirty()){
            fcm2.removeAllViewsInLayout();
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
            Log.d("hhh:","4");
        }else {
            super.onBackPressed();
            Log.d("hhh:","5");
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
                weather.iconData = ((new WeatherHTTPClient()).getImage(weather.currentCondition.getIcon()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (weather.iconData != null && weather.iconData.length > 0) {
                ImageView weatherImage = (ImageView) drawer.findViewById(R.id.weather_icon);
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                weatherImage.setImageBitmap(img);
            }

            TextView textView = (TextView) drawer.findViewById(R.id.weather_info);
            mWeatherText = weather.currentCondition.getDescr() + "\nTemp: " + weather.temperature.getTemp();
            if (textView != null) {
                textView.setText(mWeatherText);
                return;
            }

            MainActivity.this.sendMessageSlow("Z" + mWeatherText + "Z");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (id) {
            case R.id.action_settings:
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        MapFragmentManager(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void MapFragmentManager(int fragmentID) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FloatingActionButton dfb = (FloatingActionButton) findViewById(R.id.discover_fab);
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // MapViewFragment map = new MapViewFragment();

        switch (fragmentID) {
            case R.id.new_trail:

                buttonNum=1;
                //NewTrailFragment curFrag1 = new NewTrailFragment();
                ft.add(fragment_container_small,newtrailFrag, getResources().getString(R.string.new_trail_tag));
                Log.d("buttonNum2: %d", String.valueOf(buttonNum));
               // ft.add(R.id.fragment_container, map, getResources().getString(R.string.map_view_tag));
                getSupportActionBar().setTitle(getResources().getString(R.string.new_trail_tag));
                DiscoverFabOnClick(dfb, mapFragment);
                ft.addToBackStack(null);
                break;

            case R.id.view_history:
                ViewHistoryFragment curFrag2 = new ViewHistoryFragment();
                ft.add(fragment_container_med1, curFrag2, getResources().getString(R.string.view_history_tag));
                //ft.add(R.id.fragment_container, mapFragment, getResources().getString(R.string.map_view_tag));
                getSupportActionBar().setTitle(getResources().getString(R.string.view_history_tag));
                dfb.hide();
                ft.addToBackStack(null);

                // TODO:
                // Call GetDataString() on all MapImages and send to bluetooth

                break;

            case R.id.fav_trails:
                FavTrailsFragment curFrag3 = new FavTrailsFragment();
                ft.add(fragment_container_med2, curFrag3, getResources().getString(R.string.fav_trail_tag));
                // ft.add(R.id.fragment_container, mapFragment, getResources().getString(R.string.map_view_tag));
                getSupportActionBar().setTitle(getResources().getString(R.string.fav_trail_tag));
                dfb.hide();
                ft.addToBackStack(null);
                break;

            case R.id.unused_frag:
                dfb.hide();
                break;

            case R.id.nav_share:
                dfb.hide();
                break;

            case R.id.nav_send:
                dfb.hide();
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
                    setButtonText(trailButton,buttonNum);
                    //trailButton.setText("Stop");
                    buttonNum=2;
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

    int count=1;
    static DiscoverNearbyFragment DF;
    public void DiscoverFabOnClick(FloatingActionButton dfb, final MapViewFragment mv) {
        final MapViewFragment map = mv;
        dfb.show();
        dfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();
                DF = new DiscoverNearbyFragment();

                if(count%2!=0){
                    getSupportActionBar().setTitle(getResources().getString(R.string.discover_nearby_tag));
                    ft.add(R.id.fragment_container_long, DF, getResources().getString(R.string.discover_nearby_tag));
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else{
                    getSupportActionBar().setTitle(getResources().getString(R.string.new_trail_tag));
                    ft.remove(DF);
                    GetNearbyPlacesData.clearPin();
                    ft.add(R.id.fragment_container_small, newtrailFrag, getResources().getString(R.string.new_trail_tag));
                    ft.addToBackStack(null);
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
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setSubtitle(subTitle);
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
                            MainActivity.this.sendMessageSlow("Z" + mWeatherText + "Z");
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
                    Toast.makeText(getBaseContext(), readMessage + " Stars!", Toast.LENGTH_SHORT).show();
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
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check current time and show up the notification if necessary
     */
    private void Notifier() {
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
}