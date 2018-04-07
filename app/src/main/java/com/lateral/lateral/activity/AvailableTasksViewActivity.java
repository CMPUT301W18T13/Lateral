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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

//import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;
import static com.lateral.lateral.model.TaskStatus.Bidded;


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
// TODO fix white bar at the top of this activity --> appeared on my original pull
// TODO clicking seems to work but test more --> pass intents
// TODO: Get the notification "x new bids!" working (or remove it)
// TODO: Disable app rotation
// TODO: BUG: Filter isn't working anymore
// TODO: No need to store extra lists for each filter, just filter the list (which is cheap) when changing the filter
// TODO: Show some info in the filter stating what the status colors mean!
// TODO: BUG: Load all records, not just top 10
// TODO: Probably remove incremental searching
// TODO: BUG: Available Tasks still shows Assigned and Done tasks (they shouldn't be displayed here)
// TODO: --> Change getEveryTask/getAllTask to getEveryAvailableTask/getAllAvailableTask and add "Requested or Bidded" to the query
// TODO: --> Change getAllTasksByDistance to getAvailableTasksByDistance and add "Requested or Bidded" to the query
// TODO: BUG: All filters are missing "Tasks without bids (only requested)" option
// TODO: BUG: Still able to bid on assigned and done tasks
// TODO: BUG: getAllTasks() uses different number than getEveryTask()
public class AvailableTasksViewActivity extends TaskRecyclerViewActivity {

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
    private ArrayList<Task> allLocallyStoredTasks;
    private ArrayList<Task> tasksWithBids = new ArrayList<Task>();

    /* Searching variables */
    SearchView searchView;


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


        if (clickedTask.getRequestingUserId().equals(MainActivity.LOGGED_IN_USER)) {

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


        // check how we got here
        handleIntent(getIntent());

        filterSpinner = findViewById(R.id.availableFilterTasksSpinner);

        final ArrayList<String> filters = new ArrayList<>();
        filters.add("All Tasks");
        filters.add("Tasks with Bids");
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
                refreshLocalArrays(null);
                displayResultsFromFilter();
                layout.setRefreshing(false);
            }
        });
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

                searchNeeded(newText);


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


    // determins if a search should be executed given the previous query, and the new query
    public void searchNeeded(String newQuery) {
        boolean search = false;

        // user pressed space, search
        if (newQuery.length() > 0) {

            if ((newQuery.substring(newQuery.length() -1).equals(" ")) && (!newQuery.substring(newQuery.length() - 2).equals(" "))) {
                search = true;
        }

        } else if (newQuery.equals("")) {
            newQuery = null;
            search = true;
        }

        // search needed
        if (search) {
            refreshLocalArrays(newQuery);
            displayResultsFromFilter();
        }

    }



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
            clearList();
            // Search will be opened in onCreateOptionsMenu
            return;
        }


        ArrayList<Task> initializedTasks;
        String query = null;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Log.d("Available Tasks", "Got here via search button");
            clearList();
            query = intent.getStringExtra(SearchManager.QUERY);
            // TODO handle exception (no internet access crashes app)
            /*
            Leave for now
            String jsonQuery = "{\"query\": {\"multi_match\": {\"title\": {\"query\" : \"" + searchField + "\"," +
                " \"fields\" : [\"title^3\", \"description\"]}}}}";
             */
            Log.d("QUERY ", query);

        }

        refreshLocalArrays(query);
        displayResultsFromFilter();


    }

    /**
     * Returns any tasks matching the given query
     * @param query Query to check on
     */
    private void returnMatchingTask(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasks(query));
    }




    /**
     * Called when user returns from viewing a task (assuming they got to it from a recyclerView)
     * Can modify to catch specific results if needed
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIEW_TASK_REQUEST) {
            Log.d("RETURNED_FROM_VIEW_TASK", "activity result caught");
            //mAdapter.notifyItemChanged(clickedItemPosition);
            // TODO --> update task clicked when user returns
            ////refreshLocalArrays(null);
            displayResultsFromFilter();

        }
    }


//    /**
//     * Avoids onItemSelected call during initialization (spinner related)
//     * https://stackoverflow.com/questions/13397933/android-spinner-avoid-onitemselected-calls-during-initialization
//     * Answered by User: Bill Mote
//     * April 2, 2018
//     */
//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//        userIsInteracting = true;
//    }


    /**
     * on creation or refresh, fills local arrays "allLocallyStoredTasks, tasksWithBids, assignedTasks, doneTasks" with
     * their correct tasks, when the user changes the filter value, the recycler view is then set to the corresponding array
     * --> Assumes globals are correctly set prior to call
     * @param query if null, indicates user got to AvailableTasksViewActivity from button, therefore load all tasks,
     *              if not null, indicates user got to activity from a searchbar, therefore load tasks from query
     */
    public void refreshLocalArrays(String query) {

        if (query == null) {
            // did not get here via search, display all
            allLocallyStoredTasks = defaultTaskService.getEveryTask();
        } else {
            // user gave search query
            allLocallyStoredTasks = defaultTaskService.getAllTasks(query);
        }


        // clear in case we are refreshing
        tasksWithBids.clear();

        for (Task curTask : allLocallyStoredTasks) {
            TaskStatus status = curTask.getStatus();
            if (status == Bidded) {
                // extract bidded tasks
                tasksWithBids.add(curTask);
            }
        }


    }

    /**
     * displays correct tasks to recycler view based on the current filter
     * --> Assumes globals are already correctly set
     */
    public void displayResultsFromFilter() {

        if (currentFilter == 0) {
            // display refreshed all
            addTasks(allLocallyStoredTasks);

        } else if (currentFilter == 1) {
            // display refreshed bidded
            addTasks(tasksWithBids);
        }
    }



}