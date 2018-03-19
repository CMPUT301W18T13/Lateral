package com.lateral.lateral.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lateral.lateral.MainActivity;
import com.lateral.lateral.R;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.ElasticSearchController;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static com.lateral.lateral.service.UserLoginTools.hashPassword;
import static com.lateral.lateral.service.UserLoginTools.randomBytes;

public class SignUpActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPhoneNumber;
    private EditText mEmail;
    private EditText mConfirmEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;

    private View mSignUpForm;
    private View mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.usernameSignUpForm);
        mPhoneNumber = (EditText) findViewById(R.id.phoneSignUpForm);
        mEmail = (EditText) findViewById(R.id.emailSignUpForm);
        mConfirmEmail = (EditText) findViewById(R.id.emailConfirmSignUpForm);
        mPassword = (EditText) findViewById(R.id.passwordSignUpForm);
        mConfirmPassword = (EditText) findViewById(R.id.passwordConfirmSignUpForm);

        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });

        mSignUpForm = findViewById(R.id.signUpFormView);
        mProgressBar = findViewById(R.id.signUpProgress);

    }

    private void validateForm(){
        String username = mUsername.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();
        String email = mEmail.getText().toString();
        String confEmail = mConfirmEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confPassword = mConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsername.setError(getString(R.string.error_invalid_username));
            focusView = mUsername;
            cancel = true;
        } else if(!isUsernameTaken(username)){
            mUsername.setError(getString(R.string.error_username_taken));
            focusView = mUsername;
            cancel = true;
        }

        // Check for valid phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumber.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumber;
            cancel = true;
        } else if (!isPhoneNumberValid(phoneNumber)) {
            mPhoneNumber.setError(getString(R.string.error_invalid_phone_number));
            focusView = mPhoneNumber;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        // Check for a valid confirmation email address.
        if (!confEmail.equals(email)) {
            mConfirmEmail.setError(getString(R.string.error_no_match_email));
            focusView = mConfirmEmail;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid confirmation password.
        if (!confPassword.equals(password)) {
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

            String salt = randomBytes(64);

            String saltAndHash = salt + ':' + hashPassword(password, salt);
            User user = new User(username, phoneNumber, email, saltAndHash);

            DefaultUserService defaultUserService = new DefaultUserService();
            defaultUserService.post(user);
            String id = defaultUserService.getIdByUsername(username);

            Intent mainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
            mainActivityIntent.putExtra("userId", id);
            startActivity(mainActivityIntent);
        }
    }

    private boolean isUsernameValid(String username){
        return username.length() > 4 && !username.contains("@");
    }

    private boolean isUsernameTaken(String username){
        DefaultUserService defaultUserService = new DefaultUserService();
        User user = defaultUserService.getUserByUsername(username);
        return user == null;
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
