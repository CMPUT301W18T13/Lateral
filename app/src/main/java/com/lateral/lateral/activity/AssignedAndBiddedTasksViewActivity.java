/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

//import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;
import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.model.TaskStatus.Assigned;
import static com.lateral.lateral.model.TaskStatus.Bidded;
import static com.lateral.lateral.model.TaskStatus.Done;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

// Just ignore this activity for now
/**
 * Activity for viewing any assigned/bidden on tasks
 */
public class AssignedAndBiddedTasksViewActivity extends TaskRecyclerViewActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PullRefreshLayout layout;

    private DefaultTaskService defaultTaskService = new DefaultTaskService();
    private DefaultBidService defaultBidService = new DefaultBidService();

    /* Filter related variables */
    private Spinner filterSpinner;
    private int currentFilter = 0;

    /* local storage */
    private ArrayList<Task> allLocallyStoredTasks = new ArrayList<Task>();
    private ArrayList<Bid> myBids = new ArrayList<Bid>();

    /**
     * Gets the layout ID of the activity
     * @return The R-id of the activity
     */
    @Override
    protected int getResourceLayoutID(){
        return R.layout.activity_assigned_and_bidded_tasks_view;
    }

    /**
     * Gets the layout ID of the RecyclerList
     * @return The R-id of the RecyclerList
     */
    @Override
    protected int getRecyclerListID() {
        return R.id.assignedTaskViewList;
    }

    /**
     * Gets the ID of the progress bar
     * @return The R-id of the progress bar
     */
    @Override
    protected int getProgressBarID() { return R.id.bidded_task_view_progress;}

    /**
     * Gets the ID of the error message
     * @return The R-id of the error message
     */
    @Override
    protected int getErrorMessageID() {
        return R.id.assignedAndBiddedErrorWarning;
    }

    /**
     * Gets the context of the current activity
     * @return The current activity's Context
     */
    @Override
    protected Context currentActivityContext(){
        return AssignedAndBiddedTasksViewActivity.this;
    }

    /**
     * Returns the target class of the activity
     * @return The target class of the activity
     */
    @Override
    protected Class targetClass(Task clickedTask) {
        return TaskViewActivity.class;
    }


    /**
     * Called when Activity is created
     * @param savedInstanceState The saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: Check return value
        boolean success = initializeLocalArrays();
        if (success) displayResultsFromFilter();

        // listen refresh event
        layout = findViewById(R.id.AssignedAndBiddedSwipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                //returnMatchingTasks(thisUserID);
                boolean success = initializeLocalArrays();
                // TODO: Check return value
                if (success) displayResultsFromFilter();
                layout.setRefreshing(false);
            }
        });


        filterSpinner = findViewById(R.id.assignedFilterTasksSpinner);

        final ArrayList<String> filters = new ArrayList<String>();
        filters.add("All Tasks");
        filters.add("Tasks with Bids");
        filters.add("Assigned Tasks");
        filters.add("Completed Tasks");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("FILTER", "Item selected = " + filters.get(position));
                if (getUserIsInteracting()) {
                    currentFilter = position;
                    displayResultsFromFilter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("FILTER", "Item selected");

            }
        });

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

    /**
     * Called when the activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_bidded_tasks);
    }


    /**
     * Called when the activity finishes with result
     * @param requestCode The code the request was made with
     * @param resultCode The result obtained
     * @param data Any additional data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIEW_TASK_REQUEST) {
            Log.d("RETURNED_FROM_VIEW_TASK", "activity result caught");
            //mAdapter.notifyItemChanged(clickedItemPosition);
            // TODO  --> eventually change to only update position clicked
            //      -->does not work rn because list order changes when task updated
            //returnMatchingTasks(thisUserID);
            // TODO: Check return value
            boolean success = initializeLocalArrays();
            if (success) displayResultsFromFilter();
        }
    }


    /**
     * on creation or refresh, fills local arrays "allLocallyStoredTasks, tasksWithBids, assignedTasks, doneTasks" with
     * their correct tasks, when the user changes the filter value, the recycler view is then set to the corresponding array
     */
    public boolean initializeLocalArrays() {

        Task curTask;

        // clear in case we are refreshing
        allLocallyStoredTasks.clear();
        myBids.clear();

        try {
            myBids = defaultBidService.getAllBidsByUserID(LOGGED_IN_USER.getId());

            for (Bid bid : myBids) {
                curTask = defaultTaskService.getTaskByTaskID(bid.getTaskId());
                allLocallyStoredTasks.add(curTask);
            }
            return true;
        } catch (Exception e){
            ErrorDialog.show(this, "Failed to load tasks");
            return false;
        }
    }


    /**
     * displays correct tasks to recycler view based on the current filter
     * assumes globals are already correctly set
     */
    public void displayResultsFromFilter() {


        ArrayList<Task> filteredTasks = new ArrayList<Task>();
        ArrayList<Bid> filteredBids = new ArrayList<Bid>();
        int index = 0;

        if (currentFilter == 0) {
            // display refreshed all
            addTasks(allLocallyStoredTasks, myBids);
            return;
        }


        for (Task localTask : allLocallyStoredTasks) {
            TaskStatus status = localTask.getStatus();

            // extract Bidded tasks
            if (currentFilter == 1) {
                if (status == Bidded) {
                    filteredTasks.add(localTask);
                    filteredBids.add(myBids.get(index));
                }
            } else if (currentFilter == 2) {
                if (status == Assigned) {
                    filteredTasks.add(localTask);
                    filteredBids.add(myBids.get(index));
                }

            } else if (currentFilter == 3) {
                if (status == Done) {
                    filteredTasks.add(localTask);
                    filteredBids.add(myBids.get(index));
                }

            }

            index += 1;
        }

        addTasks(filteredTasks, filteredBids);

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
            finish();
        } else if (id == R.id.nav_available_tasks) {
            Intent intent = new Intent(this, AvailableTasksViewActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_bidded_tasks) {

            // We are already here, do nothing

        } else if (id == R.id.nav_qrcode){
            Intent intent = new Intent(this, ScanQRCodeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_tasks) {
            Intent intent = new Intent(this, AvailableTasksViewActivity.class);
            intent.setAction(AvailableTasksViewActivity.INTENT_OPEN_SEARCH);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_task_map){
            Intent intent = new Intent(this, TaskMapActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            if(getApplicationContext().deleteFile(USER_FILE_NAME)){
                MainActivity.LOGGED_IN_USER = null;
                Log.i("AssignedAndBiddedTasks", "File deleted");
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

