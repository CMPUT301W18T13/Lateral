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

import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

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
        returnMatchingTasks(thisUserID);


        // listen refresh event
        layout = (PullRefreshLayout) findViewById(R.id.AssignedAndBiddedSwipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                returnMatchingTasks(thisUserID);
                layout.setRefreshing(false);
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
//        getMenuInflater().inflate(R.menu.all_task_view_menu, menu);
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
            returnMatchingTasks(thisUserID);

        }
    }


}

