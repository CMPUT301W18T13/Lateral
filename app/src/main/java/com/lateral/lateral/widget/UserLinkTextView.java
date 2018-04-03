/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.widget;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lateral.lateral.Constants;
import com.lateral.lateral.R;
import com.lateral.lateral.dialog.UserInfoDialog;

// Source for idea: https://stackoverflow.com/questions/10696986

/**
 * A TextView that supports clickable username links
 */
public class UserLinkTextView extends AppCompatTextView {

    private boolean updating = false;

    public UserLinkTextView(Context context) {
        super(context);
        setProperties();
    }

    public UserLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProperties();
    }

    public UserLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setProperties();
    }

    // Set extra properties for this custom view
    private void setProperties() {
        setLinkTextColor(getResources().getColor(R.color.blue));
        setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
        setMovementMethod(LinkMovementMethod.getInstance());
        setClickable(true);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter){

        if (!updating) {
            updating = true;

            SpannableString spanString = new SpannableString(text.toString());
            createLinks(spanString);
            setText(spanString, TextView.BufferType.SPANNABLE);

            super.onTextChanged(text, start, lengthBefore, lengthAfter);

            updating = false;
        }
    }

    private void createLinks(SpannableString text) {

        int len = text.length();
        int link_start = -1;

        // Iterate over the text searching for strings formatted like "@username"
        for (int i = 0; i < len; i++){
            if (link_start == -1){
                // Match '@' at beginning of word or text
                if (text.charAt(i) == '@' &&
                    (i == 0 || Character.isWhitespace(text.charAt(i-1)))){
                    // Reset search
                    link_start = i;
                }
            }
            else{
                // Match invalid @
                if (text.charAt(i) == '@'){
                    link_start = -1;
                    continue;
                }

                boolean link_found = false;
                int link_end = -1;

                // Match end of word
                if (Character.isWhitespace(text.charAt(i))){
                    link_end = i;
                    link_found = true;
                }
                // Match end of the text
                else if (i == len - 1){
                    link_end = i + 1;
                    link_found = true;
                }

                if (link_found){
                    // Validate
                    if (link_end - link_start >= Constants.USERNAME_CHAR_MINIMUM &&
                            link_end - link_start <= Constants.USERNAME_CHAR_LIMIT) {

                        // Create link
                        text.setSpan(new UserLinkSpan(), link_start, link_end,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }
                    // Reset search
                    link_start = -1;
                }
            }
        }
    }

    // Source: https://stackoverflow.com/questions/10689997
    @Nullable
    private static Activity getActivityFromContext(@NonNull Context context){
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) return (Activity) context;
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    // Inner class for custom ClickableSpan
    private class UserLinkSpan extends ClickableSpan {
        @Override
        public void onClick(View view) {
            // Source: stackoverflow.com/questions/19750458
            TextView textView = (TextView)view;
            Spanned string = (Spanned)textView.getText();
            int start = string.getSpanStart(this) + 1; // Add 1 to remove @ symbol
            int end = string.getSpanEnd(this);

            String username = string.subSequence(start, end).toString();
            DialogFragment newFragment = UserInfoDialog.create(username);

            Activity parent = UserLinkTextView.getActivityFromContext(view.getContext());
            if (parent != null){
                newFragment.show(parent.getFragmentManager(), "dialog");
            }else{
                String error = "View requires activity context";
                Log.e("UserLinkTextView", error);
                throw new RuntimeException(error);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
