/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.model.PhotoGallery;
import com.lateral.lateral.service.Notification.NotificationServiceScheduler;
import com.lateral.lateral.dialog.BidDialog;
import com.lateral.lateral.R;
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
// TODO: (devon) Make title either offset or single-line
// TODO: (devon) BUG: Sometimes get after update fails to retrieve new changes
// TODO: (devon) Change text field to the tools text field on all views
// TODO: Set the imageView sizes correctly!

// TODO: Need to overwrite your own bid
// TODO: Ensure you can't bid on your own task
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

    private ImageView imageMain;
    private ImageView image0;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;

    /**
     * Called when activity is created
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

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

        imageMain = findViewById(R.id.task_view_image_main);
        image0 = findViewById(R.id.task_view_image_0);
        image1 = findViewById(R.id.task_view_image_1);
        image2 = findViewById(R.id.task_view_image_2);
        image3 = findViewById(R.id.task_view_image_3);
        image4 = findViewById(R.id.task_view_image_4);
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
            currentBid.setText(R.string.task_view_no_bids);
        } else {
            currentBid.setText(getString(R.string.dollar_amount_display,
                    String.valueOf(task.getLowestBid().getAmount())));
        }

        title.setText(task.getTitle());
        username.setText(getString(R.string.username_display, task.getRequestingUser().getUsername()));
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());

        setImages();
    }

    private void setImages() {
        // TODO: Testing
        return;
//        PhotoGallery gallery = task.getPhotoGallery();
//
//        Bitmap image;
//        if ((image = gallery.get(0)) == null){
//            imageMain.setImageResource(R.drawable.ic_menu_gallery);
//            image0.setImageResource(R.drawable.ic_menu_gallery);
//        } else {
//            imageMain.setImageBitmap(image);
//            image0.setImageBitmap(image);
//        }
//        if ((image = gallery.get(1)) == null) image1.setImageResource(R.drawable.ic_menu_gallery);
//        else image1.setImageBitmap(image);
//        if ((image = gallery.get(2)) == null) image2.setImageResource(R.drawable.ic_menu_gallery);
//        else image2.setImageBitmap(image);
//        if ((image = gallery.get(3)) == null) image3.setImageResource(R.drawable.ic_menu_gallery);
//        else image3.setImageBitmap(image);
//        if ((image = gallery.get(4)) == null) image4.setImageResource(R.drawable.ic_menu_gallery);
//        else image4.setImageBitmap(image);
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
     * Called when the options menu is created
     * @param menu The menu created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return true;
    }

    /**
     * Called when an options menu item is selected
     * @param item The menu item selected
     * @return The built in result of calling onOptionsItemSelected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_qrcode){
            Intent intent = new Intent(this, DisplayQRCodeActivity.class);
            intent.putExtra(DisplayQRCodeActivity.EXTRA_TASK_ID, taskID);
            startActivity(intent);
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

                task.setBidsPendingNotification(task.getBidsPendingNotification() + 1);
                task.setBidsNotViewed(task.getBidsNotViewed() + 1);

                if (task.getLowestBid() == null){
                    currentBid.setText(getString(R.string.dollar_amount_display,
                            String.valueOf(newBid.getAmount())));
                    task.setLowestBidValue(newBid.getAmount());     // nick
                } else if (newBid.getAmount().compareTo(task.getLowestBid().getAmount()) < 0){
                    task.setLowestBid(newBid);
                    currentBid.setText(getString(R.string.dollar_amount_display,
                            String.valueOf(newBid.getAmount())));
                    task.setLowestBidValue(newBid.getAmount());     // nick
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
