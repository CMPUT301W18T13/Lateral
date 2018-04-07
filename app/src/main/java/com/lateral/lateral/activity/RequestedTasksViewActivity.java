/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

/**
 * Activity to view all requested tasks
 */
public class RequestedTasksViewActivity extends TaskRecyclerViewActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;
    private DefaultTaskService defaultTaskService = new DefaultTaskService();

    private String thisUserID = LOGGED_IN_USER;
    private PullRefreshLayout layout;
    static final int ADD_EDIT_TASK_CODE = 2;

    /* Filter related variables */
    private Spinner filterSpinner;
    private int currentFilter = 0;
    //private boolean userIsInteracting;

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
        return R.layout.activity_requested_tasks_view;
    }

    /**
     * Gets the layout ID of the RecyclerList
     * @return The R-id of the RecyclerList
     */
    @Override
    protected int getRecyclerListID() {
        return R.id.requestedTaskViewList;
    }

    @Override
    protected int getProgressBarID() { return R.id.req_task_view_progress;}

    /**
     * Gets the context of the current activity
     * @return The current activity's Context
     */
    @Override
    protected Context currentActivityContext(){
        return RequestedTasksViewActivity.this;
    }

    /**
     * Returns the target class of the activity
     * @return The target class of the activity
     */
    @Override
    protected Class targetClass(Task clickedTask) {
        return MyTaskViewActivity.class;
    }

    /**
     * Called when activity is started
     * @param savedInstanceState The saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLocalArrays();
        displayResultsFromFilter();

        // Move to 'create new task' activity when fab pressed
        final FloatingActionButton addNewTaskFab = (FloatingActionButton) findViewById(R.id.addNewTaskFab);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Add New Task Pressed", "Add new task");
                Intent AddTaskIntent = new Intent(RequestedTasksViewActivity.this, AddEditTaskActivity.class);
                startActivityForResult(AddTaskIntent, ADD_EDIT_TASK_CODE);
            }
        });

        // listen refresh event
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeLocalArrays();
                displayResultsFromFilter();
                layout.setRefreshing(false);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int fabVisibility = 1;
            int approxPosition = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                approxPosition += dy;

                // if near top
                if (approxPosition < 100) {

                    // if fab hidden, show
                    if (fabVisibility == 0) {
                        addNewTaskFab.show();
                        fabVisibility = 1;
                    }

                } else {
                    // if fab shown, hide
                    if (fabVisibility == 1) {
                        addNewTaskFab.hide();
                        fabVisibility = 0;
                    }
                }

            }

        });

        filterSpinner = findViewById(R.id.requestedFilterTasksSpinner);

        final ArrayList<String> filters = new ArrayList<>();
        filters.add("All Tasks");
        filters.add("Tasks with Bids");
        filters.add("Assigned Tasks");
        filters.add("Completed Tasks");
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

    }

    /**
     * Called when the options menu is created
     * @param menu The menu to be created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /**
     * Returns any tasks matching the given query
     * @param query Query to check on
     */
    private void returnMatchingTasks(String query) {
        addTasks(defaultTaskService.getAllTasksByRequesterID(query));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("activity result invoked", "OnActivityResult");
        if (resultCode == RESULT_OK && requestCode == VIEW_TASK_REQUEST) {
            //mAdapter.notifyItemChanged(clickedItemPosition);
            // TODO --> eventually change to only update position clicked
            //      -->does not work rn because list order changes when task updated
            // For now. refreshes entire list
            Log.d("VIEW_TASK_REQUEST", "returned, update adapter");
            initializeLocalArrays();
            displayResultsFromFilter();

        } else if (resultCode == RESULT_OK && requestCode == ADD_EDIT_TASK_CODE) {
            // User wants to add a whole new task
            // if added, notify data set added
            // for now, refreshes entire list
            Log.d("ADD_EDIT_TASK_CODE", "returned");
            initializeLocalArrays();
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
     * --> Assumes globals are already correctly set
     */
    public void initializeLocalArrays() {
        allLocallyStoredTasks = defaultTaskService.getAllTasksByRequesterID(thisUserID);

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
     * --> Assumes globals are already correctly set
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
