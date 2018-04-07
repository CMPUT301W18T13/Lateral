/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
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
import static com.lateral.lateral.model.TaskStatus.Assigned;
import static com.lateral.lateral.model.TaskStatus.Bidded;
import static com.lateral.lateral.model.TaskStatus.Done;
import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

// Just ignore this activity for now
// TODO: Need to show my own bid along with the current lowest (unless assigned)!
/**
 * Activity for viewing any assigned/bidden on tasks
 */
public class AssignedAndBiddedTasksViewActivity extends TaskRecyclerViewActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;
    //private String thisUserID = "npwhite";          // for testing

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


}

