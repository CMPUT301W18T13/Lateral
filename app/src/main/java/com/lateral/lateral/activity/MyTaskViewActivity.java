/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.dialog.PhotoViewerDialog;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.PhotoGallery;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;
import com.lateral.lateral.widget.PhotoImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

// TODO: Need progress bar in here
public class MyTaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";
    public static final int QR_ACTIVITY_CODE = 5;

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

    private PhotoImageView imageMain;
    private LinearLayout imageLayout;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the task id
        Intent taskIntent = getIntent();
        this.taskID = taskIntent.getStringExtra(MyTaskViewActivity.EXTRA_TASK_ID);
        if (this.taskID == null){
            setResult(RESULT_CANCELED);
            finish();
        }

        currentBid = findViewById(R.id.my_task_view_current_bid);
        title = findViewById(R.id.my_task_view_title);
        date = findViewById(R.id.my_task_view_date);
        description = findViewById(R.id.my_task_view_description);
        assignedToUsername = findViewById(R.id.my_text_view_username);
        seeBidsButton = findViewById(R.id.my_task_view_see_bids_button);
        cancelTaskButton = findViewById(R.id.my_task_view_set_requested);
        taskDoneButton = findViewById(R.id.my_task_view_set_done);

        imageMain = findViewById(R.id.my_task_view_image_main);
        imageLayout = findViewById(R.id.my_task_view_imageLayout);
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

        try {
            task = loadTask();
            refresh(task);
        } catch (ServiceException e){
            ErrorDialog.show(this, "Failed to load task");
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /** Refresh with the loaded task
     * @param task task
     */
    private void refresh(Task task) {

        title.setText(task.getTitle());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());

        if (task.getLowestBidValue() == null) {
            currentBid.setText(R.string.task_view_no_bids);
        } else {
            currentBid.setText(getString(R.string.dollar_amount_display,
                    String.valueOf(task.getLowestBidValue())));
        }

        final Bid assignedBid = task.getAssignedBid();
        if (assignedBid != null) {
            String assignedUsername = assignedBid.getBidder().getUsername();
            assignedToUsername.setText(getString(R.string.username_display, assignedUsername));
        } else {
            assignedToUsername.setText(R.string.task_view_not_assigned);
        }

        TextView statusTextView = findViewById(R.id.my_task_view_status);
        statusTextView.setText(TaskStatus.getFormattedEnum(task.getStatus()));
        setImages();
        setButtonVisibility();

        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_geo_location);
        item.setVisible(task.checkGeo());
        return true;

    }

    private void setImages() {
        PhotoGallery gallery = task.getPhotoGallery();

        imageMain.setImage(gallery.get(0));
        for (int i = 0; i < PhotoGallery.MAX_PHOTOS; i++){
            PhotoImageView view = imageLayout.findViewWithTag("image" + String.valueOf(i));
            view.setImage(gallery.get(i));
        }
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
     * @return The loaded task
     */
    private Task loadTask() throws ServiceException{

        Task task = taskService.getById(taskID);

        if (task.getAssignedBidId() != null) {
            Bid bid = bidService.getById(task.getAssignedBidId());
            task.setAssignedBid(bid);
            User user = userService.getById(bid.getBidderId());
            bid.setBidder(user);
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
        startActivityForResult(intent, 0);
    }

    /**
     * Called when Set Done button is clicked
     * @param v current view
     */
    public void onSetDoneButtonClick(View v){
        try {
            task.setStatus(TaskStatus.Done);
            task.setBidsPendingNotification(0);
            task.setBidsNotViewed(0);
            taskService.update(task);
            refresh(task);
        } catch (ServiceException e){
            ErrorDialog.show(this, "Failed to update task");
        }
    }

    /**
     * Called when Set Requested (Cancel) button is clicked
     * @param v current view
     */
    public void onSetRequestedButtonClick(View v){
        try {
            bidService.delete(task.getAssignedBidId());

            task.setBidsPendingNotification(0);
            task.setBidsNotViewed(0);
            task.setAssignedBid(null);
            task.setAssignedBidId(null);
            task.setBids(null);
            task.setLowestBidValue(null);
            task.setStatus(TaskStatus.Requested);

            taskService.update(task);
            refresh(task);
        } catch (Exception e){
            ErrorDialog.show(this, "Failed to update task");
        }
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
     * @return true if handled
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
            promptDelete();
        }
        else if (item.getItemId() == R.id.action_geo_location){
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(MapActivity.EXTRA_TASK_ID, taskID);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.action_qrcode){
            Intent intent = new Intent(this, DisplayQRCodeActivity.class);
            intent.putExtra(DisplayQRCodeActivity.EXTRA_TASK_ID, taskID);
            intent.putExtra(DisplayQRCodeActivity.EXTRA_TASK_TITLE, task.getTitle());
            intent.putExtra(DisplayQRCodeActivity.EXTRA_TASK_USER, task.getRequestingUserUsername());
            startActivityForResult(intent, QR_ACTIVITY_CODE);
        }
        else if (item.getItemId() == android.R.id.home){
            setResult(RESULT_OK);
            finish();
        }
        else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_ACTIVITY_CODE){
            // Don't need refresh
            return;
        }

        if (resultCode == RESULT_OK) {
            refresh();
        }
    }

    private void promptDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this task?")
            .setCancelable(true)
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            })
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    try {
                        taskService.delete(taskID);
                        Toast postTask = Toast.makeText(MyTaskViewActivity.this,
                                "Task deleted", Toast.LENGTH_SHORT);
                        postTask.show();
                    } catch (ServiceException e){
                        ErrorDialog.show(MyTaskViewActivity.this, "Failed to delete task");
                        return;
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
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
}
