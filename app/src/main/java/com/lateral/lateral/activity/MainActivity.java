/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.Notification.NotificationServiceScheduler;
import com.lateral.lateral.service.implementation.DefaultUserService;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
// TODO: Add hamburger menu on (almost) all pages
/**
 * The main activity for the app
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    public static String LOGGED_IN_USER = null;     // tyler AWItlpZ842PX8bQQT0oq nick AWItlpRj42PX8bQQT0op

    /**
     * Called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Receive intent
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String user = extras.getString("userId");
            if (user != null) {
                LOGGED_IN_USER = user;
            }
        }
        Log.i("MainActivity", "AFT Logged in user's ID: " + LOGGED_IN_USER);

        //Start sending notifications
        NotificationServiceScheduler.scheduleNewBid(getApplicationContext());

        // Define buttons
        Button ViewRequestedTasksButton = findViewById(R.id.ViewRequestedTasksButton);
        ViewRequestedTasksButton.setOnClickListener(this);

        Button ViewAllTasksButton = findViewById(R.id.ViewAllTasksButton);
        ViewAllTasksButton.setOnClickListener(this);

        Button ViewAssignedTasksButton = findViewById(R.id.ViewAssignedTasksButton);
        ViewAssignedTasksButton.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        DefaultUserService defaultUserService = new DefaultUserService();
        User user = defaultUserService.getUserByID(LOGGED_IN_USER);

        View hView = navigationView.getHeaderView(0);
        TextView usernameView = hView.findViewById(R.id.nav_header_username);
        usernameView.setText(getString(R.string.username_display, user.getUsername()));

        TextView emailView = hView.findViewById(R.id.nav_header_email);
        emailView.setText(user.getEmailAddress());

        navigationView.setNavigationItemSelectedListener(this);
    }

    // handles onClick events

    /**
     * Handles onClick events
     * @param view The clicked view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ViewRequestedTasksButton:
                Intent ViewRequestedTasksIntent = new Intent(MainActivity.this, RequestedTasksViewActivity.class);
                startActivity(ViewRequestedTasksIntent);
                break;

            case R.id.ViewAllTasksButton:
                Intent viewAllTasksIntent = new Intent(MainActivity.this, AllTasksViewActivity.class);
                startActivity(viewAllTasksIntent);
                break;

            case R.id.ViewAssignedTasksButton:
                Intent AssignedAndBiddedTasksViewActivity = new Intent(MainActivity.this, AssignedAndBiddedTasksViewActivity.class);
                startActivity(AssignedAndBiddedTasksViewActivity);
                break;
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
     * Called when the activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * Called when the options menu is created
     * @param menu Menu created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Called when an options menu item is selected
     * @param item Options menu item
     * @return the built-in result of onOptionMenuItemSelected
     */
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

    /**
     * Handles clicking of Navigation Drawer Items
     * @param item Navigation Drawer Item
     * @return True
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_user) {
            Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_req_tasks) {
            Intent intent = new Intent(MainActivity.this, RequestedTasksViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_all_tasks) {
            Intent intent = new Intent(MainActivity.this, AllTasksViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_bidded_tasks) {
            Intent intent = new Intent(MainActivity.this, AssignedAndBiddedTasksViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_qrcode){
            Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            if(getApplicationContext().deleteFile(USER_FILE_NAME)){
                LOGGED_IN_USER = null;
                Log.i("MainActivity", "File deleted");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}