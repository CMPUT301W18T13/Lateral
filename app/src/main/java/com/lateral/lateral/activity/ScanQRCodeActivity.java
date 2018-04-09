/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import com.lateral.lateral.R;
import com.lateral.lateral.barcodereader.BarcodeCaptureActivity;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.implementation.DefaultTaskService;

public class ScanQRCodeActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    Button startButton;
    CheckBox autoFocus;
    CheckBox useFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_scan_qrcode);
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
     * Called when a certain menu item is selected
     * @param item The item selected
     * @return true if handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    /**
     * Adapted from https://github.com/googlesamples/android-vision
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_BARCODE_CAPTURE && resultCode == CommonStatusCodes.SUCCESS) {

            Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
            String url = barcode.displayValue;
            String domain = "http://lateral.lateral.com/";
            if(url.startsWith(domain)){
                String taskId = url.substring(domain.length());

                DefaultTaskService defaultTaskService = new DefaultTaskService();
                Task task;
                try {
                    task = defaultTaskService.getTaskByTaskID(taskId);
                } catch (ServiceException e){
                    ErrorDialog.show(this, "Failed to load task");
                    return;
                }

                if (task != null){
                    if (task.getStatus() == TaskStatus.Done){
                        Toast.makeText(this, "This task has already been completed", Toast.LENGTH_LONG).show();
                    }
                    else if (task.getStatus() == TaskStatus.Assigned){
                        Toast.makeText(this, "This task is already assigned", Toast.LENGTH_LONG).show();
                    } else if(task.getRequestingUserId().equals(MainActivity.LOGGED_IN_USER)){
                        Intent intent = new Intent(ScanQRCodeActivity.this, MyTaskViewActivity.class);
                        intent.putExtra(MyTaskViewActivity.EXTRA_TASK_ID, taskId);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(ScanQRCodeActivity.this, TaskViewActivity.class);
                        intent.putExtra(TaskViewActivity.EXTRA_TASK_ID, taskId);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(this, "Task could not be found", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(this, "Couldn't resolve Task from QR Code", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
