/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.dialog.UserInfoDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskList;
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

public class MyTaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private String taskID;

    private Task task;

    // UI elements
    private TextView currentBid;
    private TextView title;
    private TextView date;
    private TextView description;
    private TextView assignedToUsername;

    private Button seeBidsButton;
    private Button taskDoneButton;
    private Button cancelTaskButton;

    private TaskService taskService = new DefaultTaskService();
    private UserService userService = new DefaultUserService();
    private BidService bidService = new DefaultBidService();

    /**
     * Called when the activity is created
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task_view);

        // Get the task id
        Intent taskIntent = getIntent();
        this.taskID = taskIntent.getStringExtra(MyTaskViewActivity.EXTRA_TASK_ID);
        if (this.taskID == null){
            setResult(RESULT_CANCELED);
            finish();
        } //TODO: uncomment

        currentBid = findViewById(R.id.my_task_view_current_bid);
        title = findViewById(R.id.my_task_view_title);
        date = findViewById(R.id.my_task_view_date);
        description = findViewById(R.id.my_task_view_description);
        assignedToUsername = findViewById(R.id.my_text_view_username);
        seeBidsButton = findViewById(R.id.my_task_view_see_bids_button);
        cancelTaskButton = findViewById(R.id.my_task_view_set_requested);
        taskDoneButton = findViewById(R.id.my_task_view_set_done);
    }

    /**
     * Called when the activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("debug", "onStart called");
        refresh();
    }

    private void refresh(){

        try {
            task = loadTask(taskID);
            refresh(task);
        } catch (Exception e){
            Toast errorToast = Toast.makeText(this, "Failed to load task", Toast.LENGTH_SHORT);
            errorToast.show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /** Refresh with the loaded task
     * @param task task
     */
    private void refresh(Task task) {

        if (task.getLowestBid() == null){
            currentBid.setText(R.string.task_view_no_bids);
        } else {
            currentBid.setText(getString(R.string.dollar_amount_display,
                    String.valueOf(task.getLowestBid().getAmount())));
        }
        title.setText(task.getTitle());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());

        final Bid assignedBid = task.getAssignedBid();
        if (assignedBid != null){
            String assignedUsername = assignedBid.getBidder().getUsername();
            assignedToUsername.setText(getString(R.string.username_display, assignedUsername));
        }
        else{
            assignedToUsername.setText(R.string.task_view_not_assigned);
        }

        setButtonVisibility();
    }

    /**
     * Sets the visibility for the action buttons
     */
    private void setButtonVisibility(){

        switch (task.getStatus()){
            case Assigned:
                seeBidsButton.setVisibility(View.GONE);
                cancelTaskButton.setVisibility(View.VISIBLE);
                taskDoneButton.setVisibility(View.VISIBLE);
                break;
            case Done:
                seeBidsButton.setVisibility(View.GONE);
                cancelTaskButton.setVisibility(View.GONE);
                taskDoneButton.setVisibility(View.GONE);
                break;
            case Requested:
                seeBidsButton.setVisibility(View.GONE);
                cancelTaskButton.setVisibility(View.GONE);
                taskDoneButton.setVisibility(View.GONE);
                break;
            case Bidded:
                seeBidsButton.setVisibility(View.VISIBLE);
                cancelTaskButton.setVisibility(View.GONE);
                taskDoneButton.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Loads a task based on its taskID
     * @param taskID Task ID of the task
     * @return The loaded task
     */
    private Task loadTask(String taskID){

        Task task = taskService.getById(taskID);

        task.setLowestBid(bidService.getLowestBid(task.getId()));
        if (task.getAssignedBidId() != null) {
            Bid bid = bidService.getById(task.getAssignedBidId());
            task.setAssignedBid(bid);
            bid.setBidder(userService.getById(bid.getBidderId()));
        }
        return task;
    }

    /**
     * Called when see bid button is clicked
     * @param v The current view
     */
    public void onSeeBidButtonClick(View v){
        Intent intent = new Intent(this, BidListActivity.class);
        intent.putExtra(BidListActivity.TASK_ID, taskID);
        startActivity(intent);
    }

    /**
     * Called when Set Done button is clicked
     * @param v current view
     */
    public void onSetDoneButtonClick(View v){
        task.setStatus(TaskStatus.Done);
        taskService.update(task);
        refresh(task);
    }
    // TODO: Need some way to clearly display the status within the view

    /**
     * Called when Set Requested (Cancel) button is clicked
     * @param v current view
     */
    public void onSetRequestedButtonClick(View v){

        // TODO: BUG: Cannot save assignedBidId as null!!!!!
        Toast temp = Toast.makeText(this, "Button cannot be implemented yet", Toast.LENGTH_LONG);
        temp.show();

//        task.setAssignedBid(null);
//        task.setAssignedBidId(null);
//        task.setBids(null);
//        task.setLowestBid(null);
//        task.setStatus(TaskStatus.Requested);
//
//        bidService.deleteBidsByTask(taskID);
//        taskService.update(task);
//        refresh(task);
    }

    /**
     * Called when the options menu is created
     * @param menu The menu created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_task_menu, menu);
        return true;
    }

    /**
     * Called when an options menu item is selected
     * @param item The menu item selected
     * @return The built in result of calling onOptionsItemSelected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_edit_task){
            // Edit the task
            Intent intent = new Intent(this, AddEditTaskActivity.class);
            intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, taskID);
            startActivityForResult(intent, 2);
        }
        else if (item.getItemId() == R.id.action_delete_task){
            taskService.delete(taskID);
            Toast postTask = Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT);
            postTask.show();
            setResult(RESULT_OK);
            finish();
        }
        else if (item.getItemId() == R.id.action_qrcode){
            Intent intent = new Intent(this, DisplayQRCodeActivity.class);
            intent.putExtra(DisplayQRCodeActivity.EXTRA_TASK_ID, taskID);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode == RESULT_OK) {
            Log.e("Debug", "on activity called");
        }

    }

    public void onSeeTaskOnMap(View v){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(MapActivity.EXTRA_TASK_ID, taskID);
        startActivity(intent);

    }
}
