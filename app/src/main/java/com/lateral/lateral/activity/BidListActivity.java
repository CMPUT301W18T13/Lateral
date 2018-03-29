/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.util.ArrayList;

//TODO: fix bid limit problem

/**
 * Activity to display the list of bids
 */
public class BidListActivity extends AppCompatActivity {
    public static final String TASK_ID = "com.lateral.lateral.TASK_ID_INTERESTED_IN";

    private ListView bidListView;
    private BidRowAdapter adapter;
    private String taskID;

    /**
     * Called when activity created
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        taskID = intent.getStringExtra(TASK_ID);
        DefaultBidService bidService = new DefaultBidService();
        ArrayList<Bid> bids = bidService.getAllBidsByTaskID(taskID);

        UserService userService = new DefaultUserService();
        TaskService taskService = new DefaultTaskService();
        Task task = taskService.getById(taskID);
        task.setBidsNotViewed(0);
        taskService.update(task);
        for (Bid bid: bids){
            bid.setBidder(userService.getById(bid.getBidderId()));
        }

        adapter = new BidRowAdapter(this, bids, task, BidListActivity.this);
        adapter.setUsernameFormat(getString(R.string.bid_card_username_text_field));
        adapter.setAmountFormat(getString(R.string.bid_card_amount_text_field));
        bidListView.setAdapter(adapter);
    }

    /**
     * Called when a certain menu item is selected
     * @param item The item selected
     * @return True
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent(this, MyTaskViewActivity.class);
            returnIntent.putExtra(MyTaskViewActivity.EXTRA_TASK_ID, taskID);
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(returnIntent);
        }
        return true; //returning true produces the onActivityResult event needed
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent(this, MyTaskViewActivity.class);
        returnIntent.putExtra(MyTaskViewActivity.EXTRA_TASK_ID, taskID);
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(returnIntent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        bidListView = findViewById(R.id.bid_lv);
        bidListView.setEmptyView(empty);
    }

}
