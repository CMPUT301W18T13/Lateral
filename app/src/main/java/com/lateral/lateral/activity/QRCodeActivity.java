/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import com.lateral.lateral.MainActivity;
import com.lateral.lateral.R;
import com.lateral.lateral.barcodereader.BarcodeCaptureActivity;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.implementation.DefaultTaskService;

public class QRCodeActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    Button startButton;
    CheckBox autoFocus;
    CheckBox useFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        startButton = findViewById(R.id.qrcode_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeReaderActivity();
            }
        });

        autoFocus = findViewById(R.id.qrcode_autofocus_check);
        useFlash = findViewById(R.id.qrcode_flash_check);
    }

    private void startBarcodeReaderActivity(){
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
        intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    /**
     * Adapted from https://github.com/googlesamples/android-vision
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String url = barcode.displayValue;
                    if(url.startsWith("http://lateral.lateral.com/")){
                        String taskId = url.substring(27); // Length of URL string

                        DefaultTaskService defaultTaskService = new DefaultTaskService();
                        Task task = defaultTaskService.getTaskByTaskID(taskId);

                        if(task != null){
                            if(task.getRequestingUserId().equals(MainActivity.LOGGED_IN_USER)){
                                Toast.makeText(this, "You own this task!", Toast.LENGTH_LONG).show();
                            }
                            else if (task.getStatus() == TaskStatus.Assigned || task.getStatus() == TaskStatus.Done){
                                Toast.makeText(this, "This task has already been completed!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Intent intent = new Intent(QRCodeActivity.this, TaskViewActivity.class);
                                intent.putExtra(TaskViewActivity.EXTRA_TASK_ID, taskId);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else{
                            Toast.makeText(this, "Could not load task!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(this, "Couldn't get task data!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Log.d(TAG, "Unknown intent received!");
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
