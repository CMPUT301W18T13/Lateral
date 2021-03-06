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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.lateral.lateral.R;

import com.google.android.gms.maps.CameraUpdate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

public class TaskMapActivity extends AppCompatActivity
        implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback{

    private DefaultTaskService defaultTaskService;
    private ArrayList<Task> tasks;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private Boolean locationPermissionGranted;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_map);

        defaultTaskService = new DefaultTaskService();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnInfoWindowClickListener(this);
        checkAndGrantPermissions();
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.i("Location", "location was succesful");
                                mLastKnownLocation = location;
                                drawMapMarkers();
                            }
                        }
                    });
        }catch(SecurityException e){
            Log.i("Error","Permissions not set");
        }
    }

    private void drawMapMarkers(){
        LatLng curLoc = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        try {
            tasks = defaultTaskService.getAvailableTasksByDistance(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude(),
                    5.0);
        } catch (Exception e){
            ErrorDialog.show(this, "Failed to load tasks");
            return;
        }

        Log.i("Size", String.valueOf(tasks.size()));
        for(Task task: tasks){
            LatLng latLng = new LatLng(task.getLat(), task.getLon());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(task.getTitle())
                    .snippet(task.getStatus().toString())
                    .icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
            marker.setTag(task);
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(curLoc, 13);
        mMap.animateCamera(cameraUpdate);
    }

    public void checkAndGrantPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Task markerTask = (Task)marker.getTag();
        Class targetClass;

        if (markerTask == null) {
            return;
        }

        if (markerTask.getRequestingUserId().equals(LOGGED_IN_USER.getId())) {
            targetClass = MyTaskViewActivity.class;
        } else {
            targetClass = TaskViewActivity.class;
        }

        Intent taskIntent = new Intent(this, targetClass);
        taskIntent.putExtra(TaskViewActivity.EXTRA_TASK_ID, markerTask.getId());
        startActivity(taskIntent);
    }
}
