/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class PhotoImageView extends AppCompatImageView{
    public PhotoImageView(Context context) {
        super(context);
        setProperties();
    }

    public PhotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProperties();
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setProperties();
    }

    private void setProperties(){
        setScaleType(ScaleType.FIT_CENTER);
        setAdjustViewBounds(true);
        setBackgroundColor(Color.LTGRAY);
        // TODO: Maybe layout?
        // TODO: Set default image?
        // TODO: Spacing between elements
        // TODO: Fix the upper left images (also on task card)
        // TODO: Ensure to not open dialog if fragment manager already sees it
    }

    /**
     * This method keeps the ImageView square
     * @param widthMeasureSpec width
     * @param heightMeasureSpec height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        //noinspection SuspiciousNameCombination
        setMeasuredDimension(width, width);
    }
    // TODO: Button presses are slow for some reason
}
