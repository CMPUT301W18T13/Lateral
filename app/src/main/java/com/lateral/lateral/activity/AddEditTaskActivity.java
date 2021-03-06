/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.lateral.lateral.Constants;
import com.lateral.lateral.R;
import com.lateral.lateral.dialog.ImageSelectionDialog;
import com.lateral.lateral.dialog.PhotoViewerDialog;
import com.lateral.lateral.helper.ErrorDialog;
import com.lateral.lateral.model.PhotoGallery;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.helper.PhotoGenerator;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.widget.PhotoImageView;

import java.io.IOException;

import static com.lateral.lateral.activity.MainActivity.LOGGED_IN_USER;

/**
 * Activity to add and edit tasks
 */
public class AddEditTaskActivity extends AppCompatActivity {

    // Pass null (or nothing) to add a new task, otherwise edit the given task
    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";
    public static final int PLACE_PICKER_REQUEST = 0;
    // Note: This occupies 1000 - 1005 for each photo
    public static final int PHOTO_REQUEST = 1000;

    private String taskID;
    private Task editTask;
    private TaskService service;
    private LatLng latLng;
    private String address;
    private PhotoGallery gallery;

    // UI elements
    private EditText title;
    private EditText description;
    private Button confirmButton;
    private Button addGeoLocationButton;
    private LinearLayout imageLayout;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState The saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_edit_task);

        // Get the task id
        Intent taskIntent = getIntent();
        this.taskID = taskIntent.getStringExtra(EXTRA_TASK_ID);

        title = findViewById(R.id.add_edit_task_title);
        description = findViewById(R.id.add_edit_task_description);
        confirmButton = findViewById(R.id.add_edit_task_confirm_button);
        addGeoLocationButton = findViewById(R.id.add_geolocatio_button);
        imageLayout = findViewById(R.id.add_edit_task_imageLayout);
        setInputFilters();

        service = new DefaultTaskService();

        refresh();
    }

    /**
     * Refreshes the task
     */
    protected void refresh() {
        latLng = null;
        if (taskID == null) {
            // Add new task
            confirmButton.setText(R.string.add_task_confirm);
            setTitle(R.string.add_task_title);
            editTask = null;
            gallery = new PhotoGallery();
            // Generate default photo
            gallery.insert(new PhotoGenerator().generate(), 0);
        } else {
            // Edit existing task
            confirmButton.setText(R.string.edit_task_confirm);
            setTitle(R.string.edit_task_title);
            try {
                editTask = service.getById(taskID);
            } catch (Exception e) {
                Toast errorToast = Toast.makeText(this, "Task failed to load", Toast.LENGTH_SHORT);
                errorToast.show();
                setResult(RESULT_CANCELED);
                finish();
            }
            title.setText(editTask.getTitle());
            description.setText(editTask.getDescription());
            gallery = editTask.getPhotoGallery();
            if (editTask.checkGeo()) {
                addGeoLocationButton.setText(editTask.getAddress());
            }
        }
        refreshImages();
    }

    /**
     * Refreshes the images on the task
     */
    private void refreshImages() {
        for (int i = 0; i < PhotoGallery.MAX_PHOTOS; i++) {
            PhotoImageView view = imageLayout.findViewWithTag("image" + String.valueOf(i));
            Bitmap image = gallery.get(i);
            if (image == null) view.setImageResource(R.drawable.ic_add_image);
            else view.setImage(image);
        }
    }

    /**
     * Sets the input filters for the fields
     */
    protected void setInputFilters() {
        InputFilter[] titleFilter = new InputFilter[1];
        titleFilter[0] = new InputFilter.LengthFilter(Constants.TITLE_CHAR_LIMIT);
        title.setFilters(titleFilter);

        InputFilter[] descFilter = new InputFilter[1];
        descFilter[0] = new InputFilter.LengthFilter(Constants.DESCRIPTION_CHAR_LIMIT);
        description.setFilters(descFilter);

        confirmButton.setEnabled(false);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) confirmButton.setEnabled(false);
                else confirmButton.setEnabled(true);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Called when an options menu item is selected
     *
     * @param item The menu item selected
     * @return The built in result of calling onOptionsItemSelected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * Called when the Add/Edit Confirm button is clicked
     *
     * @param v The current view
     */
    public void onAddEditConfirmClick(View v) {

        String title = this.title.getText().toString().trim();
        String desc = this.description.getText().toString().trim();

        if (editTask == null) addNewTask(title, desc);
        else updateTask(title, desc);
    }

    public void onAddGeolocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e("Error", "Failed to load PlacePicker");
            ErrorDialog.show(this, "Geolocation service failed to open");
        }
    }

    /**
     * Adds a new task with the given title and description
     * @param title The new task's title
     * @param desc The new task's description
     */
    private void addNewTask(String title, String desc) {

        Task newTask = new Task(title, desc);
        newTask.setRequestingUserId(LOGGED_IN_USER.getId());

        String username = LOGGED_IN_USER.getUsername();
        newTask.setRequestingUserUsername(username);

        if (latLng != null) {
            newTask.setLocation(latLng.latitude, latLng.longitude, address);
        }

        newTask.setPhotoGallery(this.gallery);

        try {
            service.post(newTask);
            Toast postTask = Toast.makeText(this, "Task added", Toast.LENGTH_SHORT);
            postTask.show();
            setResult(RESULT_OK);
            finish();
        } catch (ServiceException e) {
            ErrorDialog.show(this, "Failed to post task");
        }
    }

    /**
     * Updates a task with the given title and description
     * @param title The updated title
     * @param desc The updated description
     */
    private void updateTask(String title, String desc) {

        editTask.setTitle(title);
        editTask.setDescription(desc);
        if (latLng != null)
            editTask.setLocation(latLng.latitude, latLng.longitude, address);

        try {
            service.update(editTask);
            Toast postTask = Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT);
            postTask.show();

            setResult(RESULT_OK);
            finish();
        } catch (ServiceException e){
            ErrorDialog.show(this, "Failed to update task");
        }
    }

    /**
     * Called when photo is clicked
     *
     * @param v view
     */
    public void onPhotoImageViewClick(View v) {

        PhotoImageView view = (PhotoImageView) v;
        Bitmap image = view.getImage();
        String tag = view.getTag().toString();
        // Gets the image number 0 through 4 that matches PhotoGallery index
        int photoIndex = Integer.parseInt(tag.replaceAll("\\D", ""));

        if (image != null) {
            ImageSelectionDialog dialog = new ImageSelectionDialog(this, photoIndex, image);
            dialog.setOnOptionSelectedListener(new ImageSelectionDialog.SelectionListener() {
                @Override
                public void onOptionSelected(DialogInterface dialog, int selection, int photoIndex, Bitmap image) {
                dialog.dismiss();
                if (selection == ImageSelectionDialog.MODE_IMAGE) {
                    PhotoViewerDialog viewDialog = PhotoViewerDialog.newInstance(image);
                    viewDialog.show(getFragmentManager(), "photo_dialog");
                } else if (selection == ImageSelectionDialog.MODE_REPLACE) {
                    addNewImage(photoIndex);
                } else if (selection == ImageSelectionDialog.MODE_DELETE) {
                    // Insert null to delete
                    AddEditTaskActivity.this.gallery.insert(null, photoIndex);
                    refreshImages();
                }
                }
            });
            dialog.show();

        } else {
            addNewImage(photoIndex);
        }
    }

    /**
     * Adds a new image
     * @param photoIndex index of the new photo
     */
    private void addNewImage(int photoIndex) {
        if (photoIndex < 0 || photoIndex >= PhotoGallery.MAX_PHOTOS)
            throw new IllegalArgumentException("Index out of range");

        // Send intent to search image
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_REQUEST + photoIndex);
    }

    /**
     * Called when the activity finishes with result
     * @param requestCode The code the request was made with
     * @param resultCode The result obtained
     * @param data Any additional data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place selectedPlace = PlacePicker.getPlace(this, data);
            latLng = selectedPlace.getLatLng();
            address = selectedPlace.getName().toString();
            addGeoLocationButton.setText(address);
            addGeoLocationButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_white_24dp, 0, 0, 0);
        } else if (requestCode >= PHOTO_REQUEST && resultCode == RESULT_OK) {
            // Adding new image

            try {
                Uri imageData = data.getData();
                Bitmap image = PhotoGallery.generateBitmap(getContentResolver(), imageData);
                gallery.insert(image, requestCode - PHOTO_REQUEST);
                refreshImages();
            } catch (IOException e) {
                ErrorDialog.show(this, "Image could not be loaded");
            }
        }
    }
}
