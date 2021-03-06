/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.lateral.lateral.R;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.util.ArrayList;


/**
 * Activity to display the list of bids
 */
public class BidListActivity extends AppCompatActivity {
    public static final String TASK_ID = "com.lateral.lateral.TASK_ID_INTERESTED_IN";
    private ListView bidListView;

    /**
     * Called when activity created
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        bidListView = findViewById(R.id.bid_lv);
        setContentView(R.layout.activity_bid_list); // This will call onContentChanged
    }

    /**
     * Called when activity started
     */
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String taskID = intent.getStringExtra(TASK_ID);
        DefaultBidService bidService = new DefaultBidService();
        try {

            ArrayList<Bid> bids = bidService.getAllBidsByTaskIDAmountSorted(taskID);

            UserService userService = new DefaultUserService();
            TaskService taskService = new DefaultTaskService();
            Task task = taskService.getById(taskID);
            task.setBidsNotViewed(0);
            task.setBidsPendingNotification(0);
            taskService.update(task);
            for (Bid bid : bids) {
                bid.setBidder(userService.getById(bid.getBidderId()));
            }

            BidRowAdapter adapter = new BidRowAdapter(this, bids, task, BidListActivity.this);
            adapter.setUsernameFormat(getString(R.string.bid_card_username_text_field));
            adapter.setAmountFormat(getString(R.string.bid_card_amount_text_field));
            bidListView.setAdapter(adapter);

        } catch (ServiceException e){
            ErrorDialog.show(this, "Failed to load bids");
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Called when a certain menu item is selected
     * @param item The item selected
     * @return true if handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        bidListView = findViewById(R.id.bid_lv);
        bidListView.setEmptyView(empty);
    }

}
