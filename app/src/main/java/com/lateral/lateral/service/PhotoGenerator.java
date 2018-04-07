/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class used to generate photos
 */
public class PhotoGenerator {

    private static final int size = 2;

    private static final ArrayList<Integer> colorPalette = new ArrayList<Integer>() {{
        // From Material Design level 300 colors
        add(0xFFE57373);
        add(0xFFF06292);
        add(0xFFBA68C8);
        add(0xFF9575CD);
        add(0xFF7986CB);
        add(0xFF64B5F6);
        add(0xFF4FC3F7);
        add(0xFF4DD0E1);
        add(0xFF4DB6AC);
        add(0xFF81C784);
        add(0xFFAED581);
        add(0xFFDCE775);
        add(0xFFFFF176);
        add(0xFFFFD54F);
        add(0xFFFFB74D);
        add(0xFFFF8A65);
        add(0xFFA1887F);
        add(0xFFE0E0E0);
        add(0xFF90A4AE);
    }};

    /**
     * Generate a bitmap
     * @return new bitmap
     */
    public Bitmap generate() {
        Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        int colorNum = ThreadLocalRandom.current().nextInt(0, colorPalette.size());
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorPalette.get(colorNum));
        canvas.drawPaint(paint);
        return image;
    }
}
