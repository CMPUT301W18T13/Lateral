/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lateral.lateral.R;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    public static String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID_TO_DISPLAY";

    private String taskId;
    private DefaultTaskService defaultTaskService;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_map);

        taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        defaultTaskService = new DefaultTaskService();
        try {
            task = defaultTaskService.getById(taskId);
        } catch (ServiceException e){
            ErrorDialog.show(this, "Failed to load task");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng taskCoords = new LatLng(task.getLat(), task.getLon());
        googleMap.addMarker(new MarkerOptions().position(taskCoords)
                .title(task.getTitle())
                .snippet(task.getStatus().toString())
                .icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(taskCoords, 13);
        googleMap.animateCamera(cameraUpdate);
    }

    /**
     * Called when an options menu item is selected
     * @param item The menu item selected
     * @return true if handled
     */
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

}
