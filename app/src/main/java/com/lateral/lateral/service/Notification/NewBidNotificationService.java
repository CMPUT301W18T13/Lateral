/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lateral.lateral.MainActivity;
import com.lateral.lateral.activity.BidListActivity;
import com.lateral.lateral.activity.MyTaskViewActivity;
import com.lateral.lateral.activity.RequestedTasksViewActivity;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.util.ArrayList;

public class NewBidNotificationService extends JobService{
    private static final String newBidChannelID = "new_bid";
    private static final String groupID = "new_bid_group";
    private static NotificationManager notificationManager;
    private static int notifyID = 0;
    private static boolean initialized = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        String userID = MainActivity.LOGGED_IN_USER;

        // stop scheduling the class if user has logged out
        if (userID == null){
            if (initialized) {
                finish();
                initialized = false;
            }
            Log.i("Notification End", "No new notifications will be produced");
            return true;
        }

        // Initialize if onStartJob called for the first time after login
        if (!initialized){
            createNewBidChannel();
            resetPendingBids(userID);
            initialized = true;
        }

        displayNotifications(buildNotifications(userID));
        NotificationServiceScheduler.scheduleNewBid(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void createNewBidChannel(){
        String name = "New Bid Notification"; // The user-visible name of the channel
        String description = "Receive new bid notifications on your task"; // The user-visible description of the channel.
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    notificationManager.getNotificationChannel(newBidChannelID);
            if (channel == null) {
                channel = new NotificationChannel(newBidChannelID, name, importance);
                channel.setDescription(description);
                //channel.enableVibration(true); //TODO test
                //channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private ArrayList<Notification> buildNotifications(String userID){
        TaskService taskService = new DefaultTaskService();
        BidService bidService = new DefaultBidService();
        ArrayList<Task> tasks = taskService.getAllTasksByRequesterID(userID);
        ArrayList<Bid> taskBids;
        ArrayList<Notification> notifications = new ArrayList<>();
        Bid grabbedBid;
        int numberOfBids, bidsToGrab;
        for (Task task : tasks){
            if (task == null){
                continue;
            }

            bidsToGrab = task.getBidsPendingNotification();
            //TODO: Create mutual exclusion for read and write of elastic search data
            if (bidsToGrab > 0){
                task.setBidsPendingNotification(0);
                taskService.update(task);

                taskBids = bidService.getAllBidsByTaskID(task.getId());
                numberOfBids = taskBids.size();

                for (;bidsToGrab > 0; bidsToGrab--){
                    grabbedBid = taskBids.get(numberOfBids - bidsToGrab);
                    notifications.add(buildNotification(grabbedBid, task));
                }
            }
        }
        return  notifications;
    }

    private Notification buildNotification(Bid bid, Task task){
        //Intent intent;
        //PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        UserService userService = new DefaultUserService();

        builder = new NotificationCompat.Builder(this, newBidChannelID);

        //intent = new Intent(this, MyTaskViewActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent resultIntent = new Intent(this, MyTaskViewActivity.class);
        resultIntent.putExtra(MyTaskViewActivity.EXTRA_TASK_ID, task.getId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String bidderID = bid.getBidderId();
        User bidder = userService.getById(bidderID);
        String title = "New Bid!";
        String details = bidder.getUsername() + " has bid $" + bid.getAmount() + " on your task, "
                + task.getTitle() ;

        builder.setContentTitle(title)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentText(details)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setGroup(groupID);

        return  builder.build();
    }

    private void displayNotifications(ArrayList<Notification> notifications){
        for (Notification notification : notifications){
            notificationManager.notify(notifyID, notification);
            notifyID = (notifyID + 1) % 100; //send a maximum of 100 notifications
        }
    }

    private void finish(){
        notificationManager = null;
        notifyID = 0;
    }

    private void resetPendingBids(String userID){
        TaskService taskService = new DefaultTaskService();
        Log.i("Initialized", "NewBidNotificationService initialized");
        ArrayList<Task> tasks = taskService.getAllTasksByRequesterID(userID);
        for (Task task : tasks) {
            task.setBidsPendingNotification(0);
            taskService.update(task);
        }
    }

}
