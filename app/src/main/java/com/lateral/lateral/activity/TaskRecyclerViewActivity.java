/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.baoyz.widget.PullRefreshLayout;
import com.lateral.lateral.R;
import com.lateral.lateral.dialog.PhotoViewerDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.ItemClickSupport;
import com.lateral.lateral.widget.PhotoImageView;

import java.util.ArrayList;

import static com.lateral.lateral.activity.TaskViewActivity.EXTRA_TASK_ID;
// TODO: Maybe sort by newest first instead of oldest first
/**
 * Base activity for dealing with TaskRecyclerViews
 */
public abstract class TaskRecyclerViewActivity extends AppCompatActivity {

    // test

    public RecyclerView mRecyclerView;
    private View progressView;

    protected RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;
    private ArrayList<Bid> matchingBids;

    // request code for starting 'view a task' activity
    static final int VIEW_TASK_REQUEST = 1;
    private int clickedItemPosition = 0;

    private SwipeRefreshLayout mySwipeRefreshLayout;
    private PullRefreshLayout layout;

    private static int firstVisibleInListview;
    private boolean userIsInteracting;
    private CardView errorMessage;
    /*
    public RecyclerView.Adapter getmAdapter(){
        return this.mAdapter;
    }

    public ArrayList<Task> getMatchingTasks() {
        return this.matchingTasks;
    }

    public void setmAdapter(RecyclerView.Adapter newAdapter){
        this.mAdapter = newAdapter;
    }

    public void setMatchingTasks(ArrayList<Task> newMatchingTasks) {
        this.matchingTasks = newMatchingTasks;
    }
    */

    protected abstract int getResourceLayoutID();
    protected abstract int getRecyclerListID();
    protected abstract int getProgressBarID();
    protected abstract int getErrorMessageID();
    protected abstract Context currentActivityContext();
    protected abstract Class targetClass(Task clickedTask);

    /**
     * Called on creation of the activity
     * @param savedInstanceState saved Instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RecyclerView.LayoutManager mLayoutManager;

        super.onCreate(savedInstanceState);
        setContentView(getResourceLayoutID());

        errorMessage = findViewById(getErrorMessageID());
        setErrorMessageVisibility(false);
        //errorMessage.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        matchingTasks = new ArrayList<Task>();
        matchingBids = new ArrayList<Bid>();

        // defining recycler view
        mRecyclerView = findViewById(getRecyclerListID());
        mRecyclerView.setHasFixedSize(true);

        progressView = findViewById(getProgressBarID());
        mRecyclerView.setVisibility(View.GONE);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        /*
        Listens and handles clicks on the recycler view
         */
        ItemClickSupport.addTo(mRecyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        clickedItemPosition = position;
                        Log.d("CLICK", "Click position = " + position);

                        if ((clickedItemPosition >= 0) && (clickedItemPosition < matchingTasks.size())) {

                            Task clickedTask = matchingTasks.get(position);
                            Log.d("ITEM CLICKED", "item " + (matchingTasks.get(position).getId()));
                            //String RequestingUserId = clickedTask.getRequestingUserId();

                            // create intent to go to task view given by targetClass() --> defined in child activities
                            Intent viewTaskIntent = new Intent(currentActivityContext(), targetClass(clickedTask));
                            // pass task id into intent
                            viewTaskIntent.putExtra(EXTRA_TASK_ID, (matchingTasks.get(position).getId()));
                            //startActivity(viewTaskIntent);
                            startActivityForResult(viewTaskIntent, VIEW_TASK_REQUEST);
                        }
                    }
                });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskRowAdapter(matchingTasks, this, matchingBids);
        mRecyclerView.setAdapter(mAdapter);





    }

    /**
     * Called on creation of the options menu
     * @param menu Menu created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the options menu from XML

//        /* uncomment to add search bar icon to every child recycler view */
//
//        getMenuInflater().inflate(R.menu.available_task_view_menu, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//        /* also remove the code from the onCreateOptionsMenu in AvailableTasksViewActivity */




        //searchView.setIconifiedByDefault(true);

        return true;
    }


    /**
     * Adds a list of Tasks to the displayed tasks
     * @param returnedTasks tasks to be added
     * @param returnedBids null unless you are in Assigned and Bidded tasks view activity,
     *                     pass an array of every bid that user has made
     */
    public void addTasks(ArrayList<Task> returnedTasks, ArrayList<Bid> returnedBids) {
        Log.i("addTasks", "Showing progress bar");
        showProgress(true);
        Log.i("addTasks", "Progress bar shown");

        // most likely need to clear screen of tasks first
        //clearList();
        matchingTasks.clear();
        matchingTasks.addAll(returnedTasks);

        if (returnedBids != null) {
            matchingBids.clear();
            matchingBids.addAll(returnedBids);
        }
//        mAdapter.notifyItemInserted(matchingTasks.size() - 1);
        mAdapter.notifyDataSetChanged();


        Log.i("addTasks", "Hiding progress bar");
        showProgress(false);
        Log.i("addTasks", "Progress bar hidden");

    }

    /**
     * Clears the list of displayed tasks
     */
    public void clearList() {
        final int size = matchingTasks.size();
        matchingTasks.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
    }

    /**
     * Shows the progress UI and hides the login form.
     * @param show Whether to show or hide the progress wheel
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Called when photo is clicked
     * @param v view
     */
    public void onPhotoImageViewClick(View v){

        Bitmap image = ((PhotoImageView)v).getImage();

        if (image != null){
            PhotoViewerDialog dialog = PhotoViewerDialog.newInstance(image);
            dialog.show(getFragmentManager(), "photo_dialog");
        }
    }


    /**
     * Avoids onItemSelected call during initialization (spinner related)
     * https://stackoverflow.com/questions/13397933/android-spinner-avoid-onitemselected-calls-during-initialization
     * Answered by User: Bill Mote
     * April 2, 2018
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    /**
     * prevents filter from re-filling recyclerview on creation (since default filter is all tasks,
     * display all tasks was being invoked again)
     * @return false during creating, true if user changed filter
     */
    public boolean getUserIsInteracting() {
        return this.userIsInteracting;
    }

    /**
     * call if you want to set a generic error message on one of the task recycler views
     * @param visibility true is you want error message to be displayed, false if you
     */
    public void setErrorMessageVisibility(boolean visibility) {
        if (visibility) {
            // display error message
            errorMessage.setVisibility(View.VISIBLE);
        } else if (!visibility){
            errorMessage.setVisibility(View.INVISIBLE);
        }
    }
}
