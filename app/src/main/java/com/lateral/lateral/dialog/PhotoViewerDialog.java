/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lateral.lateral.R;

/**
 * Dialog to display bitmaps
 */
public class PhotoViewerDialog extends DialogFragment {

    private final static String IMAGE_ARG = "image";

    /**
     * Create a new instance of PhotoViewerDialog with the provided image
     * @param image bitmap image to display
     * @return new instance of PhotoViewerDialog
     */
    public static PhotoViewerDialog newInstance(Bitmap image) {
        PhotoViewerDialog dialog = new PhotoViewerDialog();

        // Set image argument
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_ARG, image);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Extra image from args
        Bitmap image = getArguments().getParcelable(IMAGE_ARG);

        // Create image view
        final ImageView view = createImageView();
        view.setImageBitmap(image);

        // Create dialog with style
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_photo_viewer_fullscreen);
        dialog.setContentView(view);
        return dialog;
    }

    private ImageView createImageView(){
        final ImageView view = new ImageView(getActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setAdjustViewBounds(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }
}
