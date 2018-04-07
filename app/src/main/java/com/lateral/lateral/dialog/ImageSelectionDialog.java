/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

/**
 * Dialog that gives options upon selecting an image
 */
public class ImageSelectionDialog implements DialogInterface{

    /**
     * Listener for Selection
     */
    public interface SelectionListener {
        void onOptionSelected(DialogInterface dialog, int selection, int photoIndex, Bitmap image);
    }

    public static int MODE_IMAGE = 0;
    public static int MODE_REPLACE = 1;
    public static int MODE_DELETE = 2;

    private final AlertDialog dialog;
    private final Bitmap image;
    private final int photoIndex;

    private SelectionListener selectionListener;

    /**
     * Create ImageSelectionDialog
     * @param context context
     * @param photoIndex index of photo in gallery
     * @param image image
     */
    public ImageSelectionDialog(Context context, final int photoIndex, Bitmap image){
        this.photoIndex = photoIndex;
        this.image = image;

        CharSequence options[] = new CharSequence[] {"View Image", "Replace Image", "Delete Image"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setItems(options, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectionListener != null){
                    selectionListener.onOptionSelected(dialog, which,
                            ImageSelectionDialog.this.photoIndex,
                            ImageSelectionDialog.this.image);
                }
            }
        });
        this.dialog = builder.create();
    }

    public void setOnOptionSelectedListener(SelectionListener listener){
        this.selectionListener = listener;
    }

    /**
     * Show dialog
     */
    public void show(){
        dialog.show();
    }

    /**
     * Cancel dialog
     */
    @Override
    public void cancel() {
        dialog.cancel();
    }

    /**
     * Dismiss dialog
     */
    @Override
    public void dismiss() {
        dialog.dismiss();
    }
}
