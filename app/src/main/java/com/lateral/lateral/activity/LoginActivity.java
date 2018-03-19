package com.lateral.lateral.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lateral.lateral.MainActivity;
import com.lateral.lateral.R;
import com.lateral.lateral.service.implementation.DefaultUserService;

import static com.lateral.lateral.service.UserLoginTools.hashPassword;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity{

    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    /**
     * Called when the activity is started
     * @param savedInstanceState The saved instance to run from
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = findViewById(R.id.username);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView mSignUpTextView = findViewById(R.id.signupPrompt);
        mSignUpTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpActivityIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpActivityIntent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
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

            DefaultUserService defaultUserService = new DefaultUserService();
            String saltAndHash = defaultUserService.getSaltAndHash(username);
            String id = defaultUserService.getIdByUsername(username);

            mAuthTask = new UserLoginTask(password, saltAndHash, id);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Validates whether the username matches the complexity requirements
     * @param username Username to validate
     * @return True if it matches requirements; false otherwise
     */
    private boolean isUsernameValid(String username) {
        return username.length() >= 4;
    }


    /**
     * Validates whether the password matches the complexity requirements
     * @param password Password to validate
     * @return True if it matches requirements; false otherwise
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     * @param show Whether to show or hide the progress wheel
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPassword;
        private final String mSaltAndHash;
        private final String mId;

        /**
         * Constructor for the UserLoginTask
         * @param password The typed password
         * @param saltAndHash The stored salt, and hashed password, concatenated by ':'
         * @param id The corresponding user's ID
         */
        UserLoginTask(String password, String saltAndHash, String id) {
            mPassword = password;
            mSaltAndHash = saltAndHash;
            mId = id;
        }

        /**
         * Done in the background during AsyncTask
         * @param params parameters to take - should be void
         * @return true if successful; false otherwise
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            if (mSaltAndHash != null){
                // Account exists, return true if the password matches.
                String pieces[] = mSaltAndHash.split(":");
                String salt = pieces[0];
                String storedPassword = pieces[1];
                String hashedEnteredPassword = hashPassword(mPassword, salt);

                return storedPassword.equals(hashedEnteredPassword);
            }
            Log.i("UserLoginTask", "");
            return false;
        }

        /**
         * Called after the background task is done - starts off the main instance
         * @param success Taken from the result of doInBackground
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainActivityIntent.putExtra("userId", mId);
                startActivity(mainActivityIntent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        /**
         * Called if method is cancelled
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

