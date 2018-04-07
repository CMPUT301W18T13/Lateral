/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultUserService;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

public class TaskMapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DefaultUserService defaultUserService = new DefaultUserService();
        User user = defaultUserService.getById(LOGGED_IN_USER);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();

        View hView = navigationView.getHeaderView(0);
        TextView usernameView = hView.findViewById(R.id.nav_header_username);
        if(user != null) {
            usernameView.setText(getString(R.string.username_display, user.getUsername()));
        } else{
            usernameView.setText("ERROR!");
            Toast.makeText(this, "Couldn't load user!", Toast.LENGTH_LONG).show();
        }

        TextView emailView = hView.findViewById(R.id.nav_header_email);
        if(user != null) {
            emailView.setText(user.getEmailAddress());
        }
        else{
            usernameView.setText("ERROR!");
            Toast.makeText(this, "Couldn't load user!", Toast.LENGTH_LONG).show();
        }

        navigationView.setNavigationItemSelectedListener(this);
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
        } else if (id == R.id.nav_qrcode){
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
                MainActivity.LOGGED_IN_USER = null;
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
