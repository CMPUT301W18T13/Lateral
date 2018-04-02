/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;
import static com.lateral.lateral.model.TaskStatus.Assigned;
import static com.lateral.lateral.model.TaskStatus.Bidded;
import static com.lateral.lateral.model.TaskStatus.Done;

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
// TODO: Need to open MyTaskViewActivity if the task belongs to current user
// TODO: Get the notification "x new bids!" working (or remove it)
// TODO: Disable app rotation
public class AllTasksViewActivity extends TaskRecyclerViewActivity {
    // TODO tomorrow me
    // (April 1 me)
    // --> need to figure out logic of how to filter searched tasks
    // --> should figure out why spinner is invoked on creation (list filled twice)

    DefaultTaskService defaultTaskService = new DefaultTaskService();
    private PullRefreshLayout layout;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    // 0 - all tasks
    // 1 - bidded tasks
    private int currentFilter = 0;


    private ArrayList<Task> allLocallyStoredTasks;
    private ArrayList<Task> tasksWithBids = new ArrayList<Task>();

    private Spinner filterSpinner;
    private boolean userIsInteracting;


    /**
     * Gets the layout ID of the activity
     * @return The R-id of the activity
     */
    @Override
    protected int getResourceLayoutID(){
        return R.layout.activity_all_tasks_view;
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
    protected int getProgressBarID() { return R.id.all_task_view_progress; }

    /**
     * Gets the context of the current activity
     * @return The current activity's Context
     */
    @Override
    protected Context currentActivityContext(){
        return AllTasksViewActivity.this;
    }

    /**
     * Returns the target class of the activity
     * @return The target class of the activity
     */
    @Override
    protected Class targetClass(Task clickedTask) {

        Class targetClass;

        if (clickedTask.getRequestingUserId().equals(LOGGED_IN_USER)) {

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

        filterSpinner = findViewById(R.id.allTasksSpinner);

        final ArrayList<String> filters = new ArrayList<String>();
        filters.add("All Tasks");
        filters.add("Tasks with Bids");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("FILTER", "Item selected = " + filters.get(position));
                //currentFilter = position;

//                if (currentFilter == 0) {
//                    // All tasks selected
//                    //currentFilter = 0;
//                    //tasksWithBids = allLocallyStoredTasks;
//                    addTasks(allLocallyStoredTasks);
//                } else {
//                    // Bidded tasks selected
//                    //currentFilter = 1;
//                    //tasksWithBids = extractTasksWithBids(allLocallyStoredTasks);
//                    addTasks(tasksWithBids);
//                }
                //refreshLocalArrays();
                //displayResultsFromFilter();
                if (userIsInteracting) {
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
        layout = (PullRefreshLayout) findViewById(R.id.allTasksSwipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
//                allLocallyStoredTasks = defaultTaskService.getEveryTask();
//                tasksWithBids = extractTasksWithBids(allLocallyStoredTasks);
//                if (currentFilter == 0) {
//                    addTasks(allLocallyStoredTasks);
//                } else {
//                    addTasks(tasksWithBids);
//                }
                refreshLocalArrays(null);
                displayResultsFromFilter();

                layout.setRefreshing(false);
            }
        });

//        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
//        mySwipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        //Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
//
//                        // This method performs the actual data-refresh operation.
//                        // The method calls setRefreshing(false) when it's finished.
//                        //myUpdateOperation();
//                        addTasks(defaultTaskService.getEveryTask());
//                        //Log.d("ALL TASKS", "All tasks stop refreshing");
//                        mySwipeRefreshLayout.setRefreshing(false);
//                    }
//                }
//        );

    }

    /**
     * Called when the options menu is created
     * @param menu The menu being created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
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
     * @param intent Intent and handles
     */
    private void handleIntent(Intent intent) {

        ArrayList<Task> initializedTasks;
        String query = null;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //filterSpinner.setVisibility(View.GONE);
            Log.d("ALL TASKS", "Got here via search button");
            clearList();
            query = intent.getStringExtra(SearchManager.QUERY);
            // TODO handle exception (no internet access crashes app)
            /*
            Leave for now
            String jsonQuery = "{\"query\": {\"multi_match\": {\"title\": {\"query\" : \"" + searchField + "\"," +
                " \"fields\" : [\"title^3\", \"description\"]}}}}";
             */
            //returnMatchingTask(query);
            Log.d("QUERY ", query);
            ////initializedTasks = defaultTaskService.getAllTasks(query);
//            allLocallyStoredTasks = initializedTasks;
//            tasksWithBids = extractTasksWithBids(allLocallyStoredTasks);
//            addTasks(initializedTasks);

        } //else {
            //Log.d("ALL TASKS", "Got here via button, load all");

            //ArrayList<Task> everyTask = defaultTaskService.getEveryTask();
            ////initializedTasks = defaultTaskService.getEveryTask();;
//            allLocallyStoredTasks = initializedTasks;
//            tasksWithBids = extractTasksWithBids(allLocallyStoredTasks);
//
//            //Log.d("Number of tasks", Integer.toString(everyTask.size()));
//            addTasks(initializedTasks);
        //}


        // initialized local storage of tasks
//        allLocallyStoredTasks = initializedTasks;
//        tasksWithBids = extractTasksWithBids(allLocallyStoredTasks);
//        addTasks(initializedTasks);
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




    /*
    Called when user returns from viewing a task (assuming they got to it from a recyclerView)
    Can modify to catch specific results if needed
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIEW_TASK_REQUEST) {
            Log.d("RETURNED_FROM_VIEW_TASK", "activity result caught");
            //mAdapter.notifyItemChanged(clickedItemPosition);
            // TODO --> eventually change to only update position clicked
            //      -->does not work rn because list order changes when task updated
            ////addTasks(defaultTaskService.getEveryTask());
            refreshLocalArrays(null);
            displayResultsFromFilter();

        }
    }

//    private ArrayList<Task> extractTasksWithBids(ArrayList<Task> tasksToFilter) {
//        ArrayList<Task> tasksWithBids = new ArrayList<Task>();
//
//        for (Task curTask : tasksToFilter) {
//            if (curTask.getStatus() == Bidded) {
//                // extract
//                tasksWithBids.add(curTask);
//            }
//        }
//
//        return tasksWithBids;
//    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }


    /**
     * on creation or refresh, fills local arrays "allLocallyStoredTasks, tasksWithBids, assignedTasks, doneTasks" with
     * their correct tasks, when the user changes the filter value, the recycler view is then set to the corresponding array
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
        //assignedTasks.clear();
        //doneTasks.clear();

        for (Task curTask : allLocallyStoredTasks) {
            TaskStatus status = curTask.getStatus();
            if (status == Bidded) {
                // extract
                tasksWithBids.add(curTask);
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
        }
    }



}
