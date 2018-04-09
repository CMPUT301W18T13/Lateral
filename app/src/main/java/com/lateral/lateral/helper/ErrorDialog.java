/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.helper;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.lateral.lateral.R;

public class ErrorDialog {
    public static void show(Context context, String message){

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.error_dialog, null);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        // TODO: BUG: Not working
        // TextView text = layout.findViewById(R.id.text);
        // text.setText(message);

        toast.show();
    }
}
