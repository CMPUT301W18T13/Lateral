/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
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
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;
import static com.lateral.lateral.model.TaskStatus.Bidded;
import static com.lateral.lateral.model.TaskStatus.Requested;


/*
Searching interface info
https://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget

Good resource for recycler view implementation
https://developer.android.com/guide/topics/ui/layout/recyclerview.html#Adapter

info on displaying search results in the current activity
https://developer.android.com/guide/topics/search/search-dialog.html#LifeCycle

 */

/**
 * Activity for viewing all available tasks
 */

public class AvailableTasksViewActivity extends TaskRecyclerViewActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static String INTENT_OPEN_SEARCH = "com.lateral.lateral.OPEN_SEARCH";
    DefaultTaskService defaultTaskService = new DefaultTaskService();
    private PullRefreshLayout layout;
    // 0 - available tasks
    // 1 - bidded tasks
    private int currentFilter = 0;

    /* Filter related variables*/
    private Spinner filterSpinner;
    //private boolean userIsInteracting;

    /* local storage */
    private ArrayList<Task> allLocallyStoredTasks = new ArrayList<Task>();
    //private ArrayList<Task> tasksWithBids = new ArrayList<Task>();

    /* Searching variables */
    private SearchView searchView;
    private String query;


    /**
     * Gets the layout ID of the activity
     * @return The R-id of the activity
     */
    @Override
    protected int getResourceLayoutID(){
        return R.layout.activity_available_tasks_view;
    }

    /**
     * Gets the ID of the RecyclerView
     * @return The R-id of the RecyclerView
     */
    @Override
    protected int getRecyclerListID() {
        return R.id.taskViewList;
    }

    @Override
    protected int getProgressBarID() { return R.id.available_task_view_progress; }

    @Override
    protected int getErrorMessageID() {
        return R.id.availableErrorWarning;
    }

    /**
     * Gets the context of the current activity
     * @return The current activity's Context
     */
    @Override
    protected Context currentActivityContext(){
        return AvailableTasksViewActivity.this;
    }

    /**
     * Returns the target class of the activity
     * @return The target class of the activity
     */
    @Override
    protected Class targetClass(Task clickedTask) {

        Class targetClass;


        if (clickedTask.getRequestingUserId().equals(LOGGED_IN_USER.getId())) {

            // user clicked on own task
            targetClass = MyTaskViewActivity.class;

        } else {
            // user clicked on someone else's task
            targetClass = TaskViewActivity.class;
        }

        return targetClass;
    }

    /**
     * Called when activity is started
     * @param savedInstanceState The saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check how we got here
        handleIntent(getIntent());

        filterSpinner = findViewById(R.id.availableFilterTasksSpinner);

        final ArrayList<String> filters = new ArrayList<>();
        filters.add("All Tasks");
        filters.add("Tasks with Bids");
        filters.add("Requested Tasks");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
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


        // listen refresh event
        layout = findViewById(R.id.availableTasksSwipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                // TODO: Check return value
                boolean success = refreshLocalArrays(query);
                if (success) displayResultsFromFilter();
                layout.setRefreshing(false);
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
     * Called when the activity starts
     */
    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_available_tasks);
    }

    /**
     * Called when the options menu is created
     * @param menu The menu being created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.available_task_view_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // could do initial searching in here instead --> if we are for sure removing
                // search icon from every recycler view
                Log.d("OnQueryListener", "TEXT SUBMITTED");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("OnQueryListener", "TEXT CHANGED|" + newText + "|");
                //searchNeeded(newText);                                                            // uncommenting rn will bring autoload back


                return false;
            }
        });

        // Open search immediately
        if (INTENT_OPEN_SEARCH.equals(getIntent().getAction())){
            searchItem.expandActionView();
            searchView.requestFocus();
        }
        return true;
    }


    // determines if a search should be executed given the previous query, and the new query
    public void searchNeeded(String newQuery) {
        boolean search = false;

        if ((newQuery.equals("")) && (INTENT_OPEN_SEARCH.equals(getIntent().getAction()))) {
            search = true;
            query = null;
        }

        // search needed
        if (search) {
            // TODO: Check return value
            boolean success = refreshLocalArrays(query);
            if (success) displayResultsFromFilter();
        }

    }



=======
>>>>>>> 0b3ec2a055605a171bba39135ffaf4c2c811e0a8
    /**
     * When an intent is received, handle it
     * @param intent Intent to Handle
     */
    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    // meat and potatoes of the search
    /**
     * Handles any search intents
     * NOTE: if you search by pressing return on a keyboard, the search is executed TWICE
     * @param intent Intent and handles
     */
    private void handleIntent(Intent intent) {

        if (INTENT_OPEN_SEARCH.equals(intent.getAction())){
            //clearList();
            // Search will be opened in onCreateOptionsMenu
            return;
        }


        ArrayList<Task> initializedTasks;
//        String query;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Log.d("Available Tasks", "Got here via search button");
            clearList();
            query = intent.getStringExtra(SearchManager.QUERY);
            // TODO: Check return value
            boolean success = refreshLocalArrays(query);
            if (success) displayResultsFromFilter();

        } else {
            query = null;
//            refreshLocalArrays(query);
//            displayResultsFromFilter();
        }

        refreshLocalArrays(query);
        displayResultsFromFilter();


    }

    /**
     * Called when user returns from viewing a task (assuming they got to it from a recyclerView)
     * Can modify to catch specific results if needed
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIEW_TASK_REQUEST) {
            // TODO: Check return value
            boolean success = refreshLocalArrays(query);
            if (success) displayResultsFromFilter();

        }
    }


    /**
     * on creation or refresh, fills local arrays "allLocallyStoredTasks, tasksWithBids, assignedTasks, doneTasks" with
     * their correct tasks, when the user changes the filter value, the recycler view is then set to the corresponding array
     * --> Assumes globals are correctly set prior to call
     * @param query if null, indicates user got to AvailableTasksViewActivity from button, therefore load all tasks,
     *              if not null, indicates user got to activity from a searchbar, therefore load tasks from query
     */
<<<<<<< HEAD
    public boolean refreshLocalArrays(String query) {

        try{
            if (query == null) {
                // did not search here via search, display all
                allLocallyStoredTasks = defaultTaskService.getEveryAvailableTask();
            } else {
                // user gave search query
                // TODO: Wrong, need to getAvailableTasks
                allLocallyStoredTasks = defaultTaskService.getAllTasks(query);
            }
            return true;
        } catch (ServiceException e){
            ErrorDialog.show(this, "Failed to load tasks");
            return false;
=======
    public void refreshLocalArrays(String query) {


        if (query == null) {
            // did not search here via search, display all
            allLocallyStoredTasks = defaultTaskService.getEveryAvailableTask();
        } else {
            // user gave search query
            // TODO: Wrong, need to getAvailableTasks
            allLocallyStoredTasks = defaultTaskService.getEveryAvailableTaskViaQuery(query);
>>>>>>> 0b3ec2a055605a171bba39135ffaf4c2c811e0a8
        }
    }

    /**
     * displays correct tasks to recycler view based on the current filter
     * --> Assumes globals are already correctly set
     */
    public void displayResultsFromFilter() {


        ArrayList<Task> filteredTasks = new ArrayList<Task>();

        if (currentFilter == 0) {
            // display refreshed all
            addTasks(allLocallyStoredTasks, null);
            return;
        }


        for (Task localTask : allLocallyStoredTasks) {
            TaskStatus status = localTask.getStatus();

            // extract Bidded tasks
            if (currentFilter == 1) {
                if (status == Bidded) {
                    filteredTasks.add(localTask);
                }
            } else if (currentFilter == 2) {
                if (status == Requested) {
                    filteredTasks.add(localTask);
                }
            }
        }

        addTasks(filteredTasks, null);


    }


    /**
     * Handles pressing of the back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

            // We are already here, do nothing

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
            Intent intent = new Intent(this, TaskMapActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            if(getApplicationContext().deleteFile(USER_FILE_NAME)){
                LOGGED_IN_USER = null;
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
