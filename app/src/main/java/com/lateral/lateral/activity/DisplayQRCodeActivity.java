/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lateral.lateral.MainActivity;
import com.lateral.lateral.R;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class DisplayQRCodeActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    ImageView qrCode;
    String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qrcode);
        qrCode = findViewById(R.id.qrcode_image);

        Intent taskIntent = getIntent();
        this.taskId = taskIntent.getStringExtra(MyTaskViewActivity.EXTRA_TASK_ID);
        if (this.taskId == null){
            setResult(RESULT_CANCELED);
            finish();
        }

        try{
            Bitmap bitmap = encodeAsBitmap("http://lateral.lateral.com/" + taskId);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e){
            e.printStackTrace();
        }
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
