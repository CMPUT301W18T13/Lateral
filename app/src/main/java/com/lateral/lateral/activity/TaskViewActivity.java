/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.MainActivity;
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
// TODO: (devon) Fix up this view to match MyTaskView
// TODO: (devon) BUG: Sometimes get after update fails to retrieve new changes
// TODO: (devon) Change text field to the tools text field on all views

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

        //TODO: testvvvv
        NotificationServiceScheduler.scheduleNewBid(getApplicationContext());

        /*final int NOTIFY_ID = 1002;
        NotificationManager notifManager = null;
        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
        }

        builder = new NotificationCompat.Builder(this, id);

        intent = new Intent(this, MyTaskViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setContentTitle("bid title")  // required
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                .setContentText(this.getString(R.string.app_name))  // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker("ticker")
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);*/
        //TODO: test^^^
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
