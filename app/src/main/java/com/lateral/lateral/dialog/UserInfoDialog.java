/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultUserService;

/**
 * Dialog to display user information
 */
public class UserInfoDialog extends DialogFragment {

    /**
     * Create a UserInfoDialog with the provided username
     * @param username username
     * @return new instance of UserInfoDialog
     */
    public static UserInfoDialog create(String username){
        UserInfoDialog dialog = new UserInfoDialog();
        Bundle args = new Bundle();
        args.putString("username", username);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Called on creation of dialog
     * @param savedInstanceState savedInstanceState
     * @return dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String username = getArguments().getString("username");
        DefaultUserService defaultUserService = new DefaultUserService();

        User user;
        try{
            user = defaultUserService.getUserByUsername(username);
        } catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder.setMessage(R.string.load_user_fail)
                    .setPositiveButton("OK", null)
                    .create();
        }
        if (user == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder.setMessage("User " + username + " could not be found")
                    .setPositiveButton("OK", null)
                    .create();
        }
        else {
            return createDialog(user);
        }
    }

    private Dialog createDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_profile_dialog, null);
        builder.setView(view);

        TextView usernameField = view.findViewById(R.id.user_dialog_username);
        usernameField.setText(getString(R.string.username_display, user.getUsername()));
        TextView email = view.findViewById(R.id.user_dialog_email);
        email.setText(user.getEmailAddress());
        TextView phone = view.findViewById(R.id.user_dialog_phone);
        phone.setText(user.getPhoneNumber());

        final String usernameString = user.getUsername();
        final String phoneNumber = user.getPhoneNumber();
        final String[] emailAddresses = {user.getEmailAddress()};

        ImageButton callButton = view.findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
                dismiss();
            }
        });

        ImageButton emailButton = view.findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, "I saw you on Lateral!");
                intent.putExtra(Intent.EXTRA_TEXT, "I saw you on Lateral and have an inquiry for you!\n\n\n\nSincerely,\n@" + usernameString + " on Lateral");
                startActivity(intent);
                dismiss();
            }
        });

        ImageButton messageButton = view.findViewById(R.id.message_button);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", "I saw you on Lateral and have an inquiry for you!\n\nSincerely,\n@" + usernameString + " on Lateral");
                startActivity(intent);
                dismiss();
            }
        });
        return builder.create();
    }

}
