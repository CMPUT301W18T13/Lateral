/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class NotificationServiceScheduler {

    public static void scheduleNewBid(Context context) {
        ComponentName serviceComponent =
                new ComponentName(context, NewBidNotificationService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(5* 1000); // wait at least 5 seconds
        builder.setOverrideDeadline(10 * 1000); // wait at most 10 seconds
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build()); // Will create a new class instance every time
        }
    }
}