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

import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.service.implementation.DefaultTaskService;

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
// TODO: Need to refresh on return to this
// TODO: Get the notification "x new bids!" working (or remove it)
public class AllTasksViewActivity extends TaskRecyclerViewActivity {

    DefaultTaskService defaultTaskService = new DefaultTaskService();
    private PullRefreshLayout layout;
    private SwipeRefreshLayout mySwipeRefreshLayout;
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
    protected Class targetClass() {
        return TaskViewActivity.class;
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


        // listen refresh event
        layout = (PullRefreshLayout) findViewById(R.id.allTasksSwipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                addTasks(defaultTaskService.getEveryTask());
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
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d("ALL TASKS", "Got here via search button");
            clearList();
            String query = intent.getStringExtra(SearchManager.QUERY);
            // TODO handle exception (no internet access crashes app)
            /*
            Leave for now
            String jsonQuery = "{\"query\": {\"multi_match\": {\"title\": {\"query\" : \"" + searchField + "\"," +
                " \"fields\" : [\"title^3\", \"description\"]}}}}";
             */
            returnMatchingTask(query);
        } else {
            Log.d("ALL TASKS", "Got here via button, load all");
            addTasks(defaultTaskService.getEveryTask());
        }
    }

    /**
     * Returns any tasks matching the given query
     * @param query Query to check on
     */
    private void returnMatchingTask(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasks(query));
    }


}
