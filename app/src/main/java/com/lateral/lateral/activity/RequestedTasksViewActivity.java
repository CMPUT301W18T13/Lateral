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
import android.view.Menu;
import android.view.View;

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
    protected Class targetClass() {
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
        FloatingActionButton addNewTaskFab = (FloatingActionButton) findViewById(R.id.addNewTaskFab);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddTaskIntent = new Intent(RequestedTasksViewActivity.this, AddEditTaskActivity.class);
                startActivity(AddTaskIntent);
            }
        });

        // display requested tasks from this user
        returnMatchingTasks(thisUserID);

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
     *
     * @param query
     */
    private void returnMatchingTasks(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasksByRequesterID(query));
    }


}
