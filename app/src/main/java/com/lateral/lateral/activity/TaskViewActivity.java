/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.MainActivity;
import com.lateral.lateral.dialog.BidDialog;
import com.lateral.lateral.R;
import com.lateral.lateral.dialog.UserInfoDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

// Malcolm's test task id: AWI9EJpYAJsZenWtuKsd

/**
 * Activity for viewing a certain task
 */
public class TaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private String taskID;

    private Task task;
    private BidService bidService = new DefaultBidService();
    private TaskService taskService = new DefaultTaskService();

    // UI elements
    private TextView currentBid;
    private TextView title;
    private TextView username;
    private TextView date;
    private TextView description;

    /**
     * Called when activity is created
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Get the task id
        Intent taskIntent = getIntent();
        this.taskID = taskIntent.getStringExtra(EXTRA_TASK_ID);
        if (this.taskID == null){
            setResult(RESULT_CANCELED);
            finish();
        }

        currentBid = findViewById(R.id.task_view_current_bid);
        title = findViewById(R.id.task_view_title);
        username = findViewById(R.id.task_view_username);
        date = findViewById(R.id.task_view_date);
        description = findViewById(R.id.task_view_description);
    }

    /**
     * Called when the activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();

        refresh();
    }

    private void refresh(){
        try{
            task = loadTask();
        } catch(Exception e){
            Toast errorToast = Toast.makeText(this, "Failed to load task", Toast.LENGTH_SHORT);
            errorToast.show();
        }

        if (task.getLowestBid() == null){
            //Editor complains unless I save as string then setText
            String noBidsString = "No Bids";
            currentBid.setText(noBidsString);
        } else {
            currentBid.setText(getString(R.string.dollar_amount_display,
                    String.valueOf(task.getLowestBid().getAmount())));
        }

        title.setText(task.getTitle());
        username.setText(getString(R.string.username_display, task.getRequestingUser().getUsername()));
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = UserInfoDialog.newInstance(task.getRequestingUserId());
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());
    }

    /**
     * Loads the task from the database
     * @return The loaded task
     */
    private Task loadTask(){
        UserService userService = new DefaultUserService();
        Task task = taskService.getById(taskID);
        task.setRequestingUser(userService.getById(task.getRequestingUserId()));
        task.setLowestBid(bidService.getLowestBid(task.getId()));
        return task;

    }

    /**
     * Called when the bid button is clicked
     * @param v The current view
     */
    public void onBidButtonClick(View v){
        final BidDialog bidCreationDialog=new BidDialog(TaskViewActivity.this);
        bidCreationDialog.setCanceledOnTouchOutside(false);
        bidCreationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            Bid newBid = bidCreationDialog.getNewBid();
            if (newBid != null){
                newBid.setTaskId(taskID);
                newBid.setBidderId(MainActivity.LOGGED_IN_USER);

                if (task.getLowestBid() == null){
                    currentBid.setText(getString(R.string.dollar_amount_display,
                            String.valueOf(newBid.getAmount())));
                } else if (newBid.getAmount().compareTo(task.getLowestBid().getAmount()) < 0){
                    task.setLowestBid(newBid);
                    currentBid.setText(getString(R.string.dollar_amount_display,
                            String.valueOf(newBid.getAmount())));
                }

                // TODO: Error handling required
                bidService.post(newBid);// Make sure bid has task Id
                task.setStatus(TaskStatus.Bidded);
                taskService.update(task);
            }
            }
        });
        bidCreationDialog.show();
        refresh();
    }
}
