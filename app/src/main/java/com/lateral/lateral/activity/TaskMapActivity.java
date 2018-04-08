/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultUserService;

import com.google.android.gms.maps.CameraUpdate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

public class TaskMapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback{
    private FusedLocationProviderClient mFusedLocationClient;
    private Location userLocation;
    private ArrayList<Task> closeTasks = new ArrayList<Task>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_task_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLocation = location;


                        }
                    }
                });
        setContentView(R.layout.content_task_map_view);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DefaultTaskService defaultTaskService = new DefaultTaskService();
        // Does this work?
        closeTasks = defaultTaskService.getAllTasksByDistance(userLocation.getLatitude(), userLocation.getLatitude(), 5.0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();

        View hView = navigationView.getHeaderView(0);
        TextView usernameView = hView.findViewById(R.id.nav_header_username);
        usernameView.setText(getString(R.string.username_display, LOGGED_IN_USER.getUsername()));

        TextView emailView = hView.findViewById(R.id.nav_header_email);
        emailView.setText(LOGGED_IN_USER.getEmailAddress());

        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap){
        for (int i = 0; i < closeTasks.size(); i++) {
            Task task = closeTasks.get(i);
            LatLng taskCoords = new LatLng(task.getLat(), task.getLon());
            googleMap.addMarker(new MarkerOptions().position(taskCoords)
                    .title(task.getTitle())
                    .snippet(task.getStatus().toString()));

        }
        LatLng userCoords = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userCoords, 13);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_task_map);
    }

    /**
     * Handles clicking of Navigation Drawer Items
     * @param item Navigation Drawer Item
     * @return True
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_user) {
            Intent intent = new Intent(this, EditUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_req_tasks) {
            Intent intent = new Intent(this, RequestedTasksViewActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_available_tasks) {
            Intent intent = new Intent(this, AvailableTasksViewActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_bidded_tasks) {
            Intent intent = new Intent(this, AssignedAndBiddedTasksViewActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_qrcode){
            Intent intent = new Intent(this, ScanQRCodeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_tasks) {
            Intent intent = new Intent(this, AvailableTasksViewActivity.class);
            intent.setAction(AvailableTasksViewActivity.INTENT_OPEN_SEARCH);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_task_map){

            // We are already here, do nothing

        } else if (id == R.id.nav_logout) {
            if(getApplicationContext().deleteFile(USER_FILE_NAME)){
                LOGGED_IN_USER = null;
                Log.i("RequestedTasksView", "File deleted");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
