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
import android.widget.Toast;


import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.util.ArrayList;

//import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;
import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.model.TaskStatus.Assigned;
import static com.lateral.lateral.model.TaskStatus.Bidded;
import static com.lateral.lateral.model.TaskStatus.Done;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

// Just ignore this activity for now
// TODO: Need to show my own bid along with the current lowest (unless assigned)!
/**
 * Activity for viewing any assigned/bidden on tasks
 */
public class AssignedAndBiddedTasksViewActivity extends TaskRecyclerViewActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String thisUserID = LOGGED_IN_USER;
    private PullRefreshLayout layout;

    private DefaultTaskService defaultTaskService = new DefaultTaskService();

    /* Filter related variables */
    private Spinner filterSpinner;
    private int currentFilter = 0;

    /* local storage */
    private ArrayList<Task> allLocallyStoredTasks;
    private ArrayList<Task> tasksWithBids = new ArrayList<Task>();
    private ArrayList<Task> assignedTasks = new ArrayList<Task>();
    private ArrayList<Task> doneTasks = new ArrayList<Task>();

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

    @Override
    protected int getProgressBarID() { return R.id.bidded_task_view_progress;}
    //bidded_task_view_progress

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
    // TODO probably need to change, need to hide "BID NOW!" button which will appear
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

        Log.d("LOGGED IN USER", thisUserID);
        //returnMatchingTasks(thisUserID);

        initializeLocalArrays();
        displayResultsFromFilter();


        // listen refresh event
        layout = (PullRefreshLayout) findViewById(R.id.AssignedAndBiddedSwipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                //returnMatchingTasks(thisUserID);
                initializeLocalArrays();
                displayResultsFromFilter();
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

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_bidded_tasks);
    }

    /**
     * Called when the options menu is created
     * @param menu The menu created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        //SearchView searchView;
//
//        // Inflate the options menu from XML
//        getMenuInflater().inflate(R.menu.available_task_view_menu, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        super.onCreateOptionsMenu(menu);

        return true;
    }

//    /**
//     * Adds a list of tasks to the currently displayed lists
//     * @param returnedTasks The list of tasks to add
//     */
//    private void addTasks(ArrayList<Task> returnedTasks) {
//        matchingTasks.addAll(returnedTasks);
//        mAdapter.notifyItemInserted(matchingTasks.size() - 1);
//
//    }

//    /**
//     * Clear the list of displayed tasks
//     */
//    private void clearList() {
//        final int size = matchingTasks.size();
//        matchingTasks.clear();
//        mAdapter.notifyItemRangeRemoved(0, size);
//    }

    /**
     * Adds the list of tasks matching a certain Requester ID to the list of displayed tasks
     * @param query The Requester ID to get tasks from
     */
    private void returnMatchingTasks(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getBiddedTasks(query));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIEW_TASK_REQUEST) {
            Log.d("RETURNED_FROM_VIEW_TASK", "activity result caught");
            //mAdapter.notifyItemChanged(clickedItemPosition);
            // TODO  --> eventually change to only update position clicked
            //      -->does not work rn because list order changes when task updated
            //returnMatchingTasks(thisUserID);
            initializeLocalArrays();
            displayResultsFromFilter();

        }
    }


    /**
     * on creation or refresh, fills local arrays "allLocallyStoredTasks, tasksWithBids, assignedTasks, doneTasks" with
     * their correct tasks, when the user changes the filter value, the recycler view is then set to the corresponding array
     */
    public void initializeLocalArrays() {
        allLocallyStoredTasks = defaultTaskService.getBiddedTasks(thisUserID);

        // clear in case we are refreshing
        tasksWithBids.clear();
        assignedTasks.clear();
        doneTasks.clear();

        for (Task curTask : allLocallyStoredTasks) {
            TaskStatus status = curTask.getStatus();
            if (status == Bidded) {
                // extract
                tasksWithBids.add(curTask);
            } else if (status == Assigned) {
                assignedTasks.add(curTask);
            } else if (status == Done) {
                doneTasks.add(curTask);
            }
        }


    }


    /**
     * displays correct tasks to recycler view based on the current filter
     * assumes globals are already correctly set
     */
    public void displayResultsFromFilter() {

        if (currentFilter == 0) {
            // display refreshed all
            addTasks(allLocallyStoredTasks);

        } else if (currentFilter == 1) {
            // display refreshed bidded
            addTasks(tasksWithBids);

        } else if (currentFilter == 2) {
            // display refreshed assigned
            addTasks(assignedTasks);

        } else if (currentFilter == 3) {
            // display refreshed done
            addTasks(doneTasks);
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

