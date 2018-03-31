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

import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;

/**
 * Activity to view all requested tasks
 */
public class RequestedTasksViewActivity extends TaskRecyclerViewActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;

    private String thisUserID = LOGGED_IN_USER;             // class spec       // for testing
    private PullRefreshLayout layout;
    static final int ADD_EDIT_TASK_CODE = 2;

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
        // Move to create new task activity when fab pressed
        final FloatingActionButton addNewTaskFab = (FloatingActionButton) findViewById(R.id.addNewTaskFab);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Add New Task Pressed", "Add new task");
                Intent AddTaskIntent = new Intent(RequestedTasksViewActivity.this, AddEditTaskActivity.class);
                startActivityForResult(AddTaskIntent, ADD_EDIT_TASK_CODE);
            }
        });

        // display requested tasks from this user
        returnMatchingTasks(thisUserID);


        // listen refresh event
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                returnMatchingTasks(thisUserID);
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
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasksByRequesterID(query));
    }

    // TODO update so only refreshes if new activity posted
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("activity result invoked", "OnActivityResult");
        if (requestCode == VIEW_TASK_REQUEST) {
            //mAdapter.notifyItemChanged(clickedItemPosition);
            // TODO --> eventually change to only update position clicked
            //      -->does not work rn because list order changes when task updated
            // For now. refreshes entire list
            Log.d("VIEW_TASK_REQUEST", "returned, update adapter");
            returnMatchingTasks(thisUserID);

        } else if (requestCode == ADD_EDIT_TASK_CODE) {
            // User wants to add a whole new task
            // if added, notify data set added
            // for now, refreshes entire list
            Log.d("ADD_EDIT_TASK_CODE", "returned");
            returnMatchingTasks(thisUserID);
        }
    }


}
