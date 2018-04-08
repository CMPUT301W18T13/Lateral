/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultUserService;
import com.lateral.lateral.service.notification.NotificationServiceScheduler;

import static com.lateral.lateral.Constants.USER_FILE_NAME;

/**
 * The main activity for the app
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static User LOGGED_IN_USER = null;

    /**
     * Called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Receive intent
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String user = extras.getString("userId");
            if (user != null) {
                DefaultUserService defaultUserService = new DefaultUserService();
                LOGGED_IN_USER = defaultUserService.getById(user);
            }
        }
        Log.i("MainActivity", "AFT Logged in user's ID: " + LOGGED_IN_USER.getId());

        //Start sending notifications
        NotificationServiceScheduler.scheduleNewBid(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);
        TextView usernameView = hView.findViewById(R.id.nav_header_username);
        usernameView.setText(getString(R.string.username_display, LOGGED_IN_USER.getUsername()));

        TextView emailView = hView.findViewById(R.id.nav_header_email);
        emailView.setText(LOGGED_IN_USER.getEmailAddress());

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Uncheck all options
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();
        int size = navMenu.size();
        for (int i = 0; i < size; i++) {
            navMenu.getItem(i).setChecked(false);
        }
    }

    // handles onClick events

    /**
     * Handles button onClick events
     * @param view The clicked view
     */
    public void onButtonClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.button_available:
                intent = new Intent(MainActivity.this, AvailableTasksViewActivity.class);
                startActivity(intent);
                break;

            case R.id.button_requested:
                intent = new Intent(MainActivity.this, RequestedTasksViewActivity.class);
                startActivity(intent);
                break;

            case R.id.button_search:
                intent = new Intent(MainActivity.this, AvailableTasksViewActivity.class);
                intent.setAction(AvailableTasksViewActivity.INTENT_OPEN_SEARCH);
                startActivity(intent);
                break;

            case R.id.button_assigned:
                intent = new Intent(MainActivity.this, AssignedAndBiddedTasksViewActivity.class);
                startActivity(intent);
                break;

            case R.id.button_map:
                intent = new Intent(MainActivity.this, TaskMapActivity.class);
                startActivity(intent);
                break;

            case R.id.button_QR:
                intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
                startActivity(intent);
        }
    }

    /**
     * Handles pressing of the back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        } else if (id == R.id.nav_available_tasks) {
            Intent intent = new Intent(this, AvailableTasksViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_bidded_tasks) {
            Intent intent = new Intent(this, AssignedAndBiddedTasksViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_qrcode) {
            Intent intent = new Intent(this, ScanQRCodeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_tasks) {
            Intent intent = new Intent(this, AvailableTasksViewActivity.class);
            intent.setAction(AvailableTasksViewActivity.INTENT_OPEN_SEARCH);
            startActivity(intent);
        } else if (id == R.id.nav_task_map){
            Intent intent = new Intent(this, TaskMapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            if(getApplicationContext().deleteFile(USER_FILE_NAME)){
                LOGGED_IN_USER = null;
                Log.i("MainActivity", "File deleted");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
