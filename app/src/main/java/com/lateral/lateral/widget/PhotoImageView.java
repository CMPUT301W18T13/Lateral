/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

// TODO: Change ic_menu_gallery so blank ImageViews look nice
// TODO: Add (+) icon for adding new images

import com.lateral.lateral.R;

/**
 * Custom imageview for displaying task photos
 */
public class PhotoImageView extends AppCompatImageView{

    /**
     * Create PhotoImageView
     * @param context context
     */
    public PhotoImageView(Context context) {
        super(context);
        setProperties();
    }

    /**
     * Create PhotoImageView
     * @param context context
     * @param attrs attributes
     */
    public PhotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProperties();
    }

    /**
     * Create PhotoImageView
     * @param context context
     * @param attrs attributes
     * @param defStyleAttr style attribute
     */
    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setProperties();
    }

    /**
     * Set image using bitmap, if passed null it will assign a default resource
     * @param image bitmap image
     */
    public void setImage(Bitmap image){
        if (image == null) setImageResource(R.drawable.ic_menu_gallery);
        else setImageBitmap(image);
    }

    /**
     * Get the bitmap image, or null is the image is not a bitmap
     * @return Bitmap
     */
    public Bitmap getImage(){
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        } else return null;
    }

    private void setProperties(){
        setScaleType(ScaleType.FIT_CENTER);
        setAdjustViewBounds(true);
        setBackgroundColor(Color.LTGRAY);
        setImageResource(R.drawable.ic_menu_gallery);
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
    // TODO: Fast double click may open the dialog twice
}
