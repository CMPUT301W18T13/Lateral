/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.widget.UserLinkTextView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class DisplayQRCodeActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    ImageView qrCode;
    TextView taskTitleView;
    UserLinkTextView userStringView;
    String taskId;
    String taskTitle;
    String taskUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_display_qrcode);
        qrCode = findViewById(R.id.qrcode_image);

        taskTitleView = findViewById(R.id.qrcode_task_title);
        userStringView = findViewById(R.id.qrcode_user_view);

        Intent taskIntent = getIntent();
        this.taskId = taskIntent.getStringExtra(MyTaskViewActivity.EXTRA_TASK_ID);
        this.taskTitle = taskIntent.getStringExtra(MyTaskViewActivity.EXTRA_TASK_TITLE);
        this.taskUsername = taskIntent.getStringExtra(MyTaskViewActivity.EXTRA_TASK_USER);
        if (this.taskId == null || this.taskUsername == null || this.taskTitle == null){
            setResult(RESULT_CANCELED);
            finish();
        }

        try{
            Bitmap bitmap = encodeAsBitmap("http://lateral.lateral.com/" + taskId);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e){
            e.printStackTrace();
        }

        taskTitleView.setText(taskTitle);
        userStringView.setText(getString(R.string.qrcode_user, taskUsername));

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
     * Adapted from https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code
     * Returns a QR code encoding of a string
     * @param str String to encode
     * @return QR Code bitmap
     * @throws WriterException
     */
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 200, 200, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, w, h);
        return bitmap;
    }
}
