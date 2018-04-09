/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultUserService;

import static com.lateral.lateral.service.UserLoginService.hashPassword;
import static com.lateral.lateral.service.UserLoginService.isEmailValid;
import static com.lateral.lateral.service.UserLoginService.isPasswordValid;
import static com.lateral.lateral.service.UserLoginService.isPhoneNumberValid;
import static com.lateral.lateral.service.UserLoginService.login;
import static com.lateral.lateral.service.UserLoginService.randomBytes;
import static com.lateral.lateral.service.UserLoginService.saveUserToken;
// TODO: Username should not be editable!!! It breaks other parts of the app if user changes name
// TODO: Editing username gives "Username already taken error"
// TODO: Wrong validation message for email address
public class EditUserActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPhoneNumber;
    private EditText mEmail;
    private EditText mConfirmEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;

    private View mSignUpForm;
    private View mProgressBar;

    private User mCurrentUser = MainActivity.LOGGED_IN_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sign_up);

        // Set up form views
        mUsername = findViewById(R.id.usernameSignUpForm);
        mUsername.setVisibility(View.INVISIBLE);

        TextView usernameDisplay = findViewById(R.id.usernameDisplay);
        usernameDisplay.setText(mCurrentUser.getUsername());

        mPhoneNumber = findViewById(R.id.phoneSignUpForm);
        mPhoneNumber.setHint(R.string.prompt_change_phone);

        mEmail = findViewById(R.id.emailSignUpForm);
        mEmail.setHint(R.string.prompt_change_email);

        mConfirmEmail = findViewById(R.id.emailConfirmSignUpForm);

        mPassword = findViewById(R.id.passwordSignUpForm);
        mConfirmPassword = findViewById(R.id.passwordConfirmSignUpForm);
        mPassword.setHint(R.string.prompt_change_password);

        // Watch for "Enter" click on the keyboard
        mConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    validateForm();
                    return true;
                }
                return false;
            }
        });

        // Set up button
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });

        mSignUpForm = findViewById(R.id.signUpFormView);
        mProgressBar = findViewById(R.id.signUpProgress);
    }

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
     * Validates the info entered, and creates and uploads the user if everything checks out
     */
    private void validateForm(){
        String phoneNumber = mPhoneNumber.getText().toString();
        String email = mEmail.getText().toString();
        String confEmail = mConfirmEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confPassword = mConfirmPassword.getText().toString();

        boolean cancel = false;
        boolean newPhoneNumber = true;
        boolean newEmail = true;
        boolean newPassword = true;
        View focusView = null;

        // Check for valid phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            newPhoneNumber =false;
        } else if (!isPhoneNumberValid(phoneNumber)) {
            mPhoneNumber.setError(getString(R.string.error_invalid_phone_number));
            focusView = mPhoneNumber;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            newEmail = false;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        // Check for a valid confirmation email address.
        if (!confEmail.equals(email) && newEmail) {
            mConfirmEmail.setError(getString(R.string.error_no_match_email));
            focusView = mConfirmEmail;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            newPassword = false;
        }
        else if (!isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid confirmation password.
        if (!confPassword.equals(password) && newPassword) {
            mConfirmPassword.setError(getString(R.string.error_no_match_password));
            focusView = mConfirmPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            // Update the user
            if(newPhoneNumber) {
                mCurrentUser.setPhoneNumber(phoneNumber);
            }
            if(newEmail) {
                mCurrentUser.setEmailAddress(email);
            }
            if(newPassword) {
                // Generate secure salt, and hash the password with it
                String salt = randomBytes(64);
                String saltAndHash = salt + ':' + hashPassword(password, salt);
                mCurrentUser.setSaltAndHash(saltAndHash);
            }

            // If there's anything new
            if(newPassword || newEmail || newPhoneNumber) {
                DefaultUserService defaultUserService = new DefaultUserService();
                defaultUserService.update(mCurrentUser);
            }

            // Start the next activity with the user logged in
            saveUserToken(mCurrentUser, getApplicationContext());
            login(mCurrentUser.getId(), getApplicationContext());
            finish();
        }
    }

    /**
     * Shows the progress UI and hides the signup form.
     * @param show Whether to show or hide the progress wheel
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mSignUpForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        // The ViewPropertyAnimator APIs are not available, so simply show
        // and hide the relevant UI components.
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
