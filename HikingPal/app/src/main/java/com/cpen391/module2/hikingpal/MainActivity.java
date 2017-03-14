package com.cpen391.module2.hikingpal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cpen391.module2.hikingpal.fragment.DiscoverNearbyFragment;
import com.cpen391.module2.hikingpal.fragment.FavTrailsFragment;
import com.cpen391.module2.hikingpal.fragment.NewTrailFragment;
import com.cpen391.module2.hikingpal.fragment.ViewHistoryFragment;
import com.cpen391.module2.hikingpal.fragment.MapViewFragment;

import static com.cpen391.module2.hikingpal.R.id.fragment_container;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_med1;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_med2;
import static com.cpen391.module2.hikingpal.R.id.fragment_container_small;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_ALL_MAP_PERMISSIONS = 1;
    MapViewFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflate the container
        setContentView(R.layout.activity_main);
        
        obtainPermissions();

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

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mapFragment = new MapViewFragment();
        ft.add(fragment_container, mapFragment, "googleMap");
        ft.addToBackStack(null);
        ft.commit();
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

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();

        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout fcl = (FrameLayout)findViewById(R.id.fragment_container_long);
        FrameLayout fcs = (FrameLayout)findViewById(fragment_container_small);
        FrameLayout fcm1 = (FrameLayout)findViewById(fragment_container_med1);
        FrameLayout fcm2 = (FrameLayout)findViewById(fragment_container_med2);
        FloatingActionButton dfb = (FloatingActionButton) findViewById(R.id.discover_fab);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(fcl.isDirty()){
            fcl.removeAllViewsInLayout();
            ft.add(R.id.fragment_container_small, new NewTrailFragment(), getResources().getString(R.string.new_trail_tag));
            ft.add(R.id.fragment_container, new MapViewFragment(), getResources().getString(R.string.map_view_tag));
            ft.addToBackStack(null);
            ft.commit();
        }else if(fcs.isDirty()){
            fcs.removeAllViewsInLayout();
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
        }else if(fcm1.isDirty()){
            fcm1.removeAllViewsInLayout();
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
        }else if(fcm2.isDirty()){
            fcm2.removeAllViewsInLayout();
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            dfb.hide();
        }else {
            super.onBackPressed();
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
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FloatingActionButton dfb = (FloatingActionButton) findViewById(R.id.discover_fab);

        FragmentTransaction ft = fm.beginTransaction();

        switch (fragmentID) {
            case R.id.new_trail:
                ft.add(fragment_container_small, new NewTrailFragment(), getResources().getString(R.string.new_trail_tag));
                ft.add(R.id.fragment_container, new MapViewFragment(), getResources().getString(R.string.map_view_tag));
                getSupportActionBar().setTitle(getResources().getString(R.string.new_trail_tag));
                DiscoverFabOnClick(dfb);
                ft.addToBackStack(null);
                break;

            case R.id.view_history:
                ft.add(fragment_container_med1, new ViewHistoryFragment(), getResources().getString(R.string.view_history_tag));
                ft.add(R.id.fragment_container, new MapViewFragment(), getResources().getString(R.string.map_view_tag));
                getSupportActionBar().setTitle(getResources().getString(R.string.view_history_tag));
                dfb.hide();
                ft.addToBackStack(null);
                break;

            case R.id.fav_trails:
                ft.add(fragment_container_med2, new FavTrailsFragment(), getResources().getString(R.string.fav_trail_tag));
                ft.add(R.id.fragment_container, new MapViewFragment(), getResources().getString(R.string.map_view_tag));
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

    public void DiscoverFabOnClick(FloatingActionButton dfb) {
        dfb.show();
        dfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();

                getSupportActionBar().setTitle(getResources().getString(R.string.discover_nearby_tag));
                ft.add(R.id.fragment_container, new MapViewFragment(), getResources().getString(R.string.map_view_tag));
                ft.add(R.id.fragment_container_long, new DiscoverNearbyFragment(), getResources().getString(R.string.discover_nearby_tag));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }
}