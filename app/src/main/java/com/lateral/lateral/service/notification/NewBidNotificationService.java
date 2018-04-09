/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.notification;

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
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.lateral.lateral.R;
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

import static com.lateral.lateral.service.UserLoginService.loadUserFromToken;

public class NewBidNotificationService extends JobService{
    private static final String newBidChannelID = "new_bid";
    private static final String groupID = "new_bid_group";
    private static NotificationManager notificationManager;
    private static int notifyID = 0;
    private static final int summaryNotifyID = 1111;
    private static boolean initialized = false;
    private static long time_initialized;
    private static String previousUserID = null;
    private static Notification summaryNotification = null;

    @Override
    public boolean onStartJob(JobParameters params) {
        String userID = loadUserFromToken(getApplicationContext());

        /*ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.search(0).topActivity;
        if(!componentInfo.getPackageName().equals("com.lateral.lateral")) {
            Log.e("test", "background");
        }*/

        // stop scheduling the class if user has logged out
        if (userID == null){
            notificationManager = null;
            notifyID = 0;
            initialized = false;
            Log.i("Notification End", "No new notifications will be produced");
            return false;
        }

        // Initialize if onStartJob called for the first time after login
        if (!initialized || previousUserID.compareTo(userID) != 0){
            createNewBidChannel();
            buildSummaryNotification();
            resetPendingBids(userID);
            time_initialized = System.currentTimeMillis();
            initialized = true;
        }

        // If we have given enough time for the database to reset pending bids,
        // check for pending notifications, and then , build, and display them
        if ((System.currentTimeMillis() - time_initialized) > 5000){
            displayNotifications(buildNotifications(userID));
        }

        previousUserID = userID;
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
                //channel.enableVibration(true);
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
            if (bidsToGrab > 0){
                taskBids = bidService.getAllBidsByTaskIDAmountSorted(task.getId());


                numberOfBids = taskBids.size();

                if (numberOfBids - bidsToGrab < 0){
                    Log.e("Warning", "Index error avoided in NewBidNotificationService" +
                            "with numberOfBids="+String.valueOf(numberOfBids)+
                            " and bidsToGrab="+String.valueOf(bidsToGrab));
                    task.setBidsPendingNotification(0);
                    taskService.update(task);
                    continue;
                }

                task.setBidsPendingNotification(0);
                taskService.update(task);

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

        //create stupid god damn activity stack
        Intent resultIntent = new Intent(this, RequestedTasksViewActivity.class);
        //resultIntent.putExtra(MyTaskViewActivity.EXTRA_TASK_ID, task.getId());
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
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_lateral_notification)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setContentText(details)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setGroup(groupID);

        return  builder.build();
    }

    private void buildSummaryNotification(){
        //create stupid god damn activity stack
        Intent resultIntent = new Intent(this, RequestedTasksViewActivity.class);
        //resultIntent.putExtra(MyTaskViewActivity.EXTRA_TASK_ID, task.getId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        summaryNotification = new NotificationCompat.Builder(this, newBidChannelID)
                        .setSmallIcon(R.drawable.ic_lateral_notification)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setSummaryText("new bids!"))
                        .setGroup(groupID)
                        .setGroupSummary(true)
                        .build();
    }

    private void displayNotifications(ArrayList<Notification> notifications){
        for (Notification notification : notifications){
            notificationManager.notify(notifyID, notification);
            notificationManager.notify(summaryNotifyID, summaryNotification);
            notifyID = (notifyID + 1) % 100; //send a maximum of 100 notifications
        }
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
