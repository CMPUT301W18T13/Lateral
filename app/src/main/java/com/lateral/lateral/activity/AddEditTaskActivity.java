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
import android.provider.MediaStore;
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

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.lateral.lateral.Constants;
import com.lateral.lateral.R;
import com.lateral.lateral.dialog.ImageSelectionDialog;
import com.lateral.lateral.dialog.PhotoViewerDialog;
import com.lateral.lateral.model.PhotoGallery;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.PhotoGenerator;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;
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
            if(editTask.checkGeo()){
                addGeoLocationButton.setText(editTask.getAddress());
            }
        }
        refreshImages();
    }

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
        // TODO: BUG: Need to be able to edit this field
        // TODO: BUG: Button states shows up incorrectly when editing
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            String error = "Error occurred loading PlacePicker";
            Log.i("AddEditTaskActivity", error);
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewTask(String title, String desc) {

        try {
            Task newTask = new Task(title, desc);
            newTask.setRequestingUserId(LOGGED_IN_USER);

            // TODO vvvvvvvvvv create variable LOGGED_IN_USER_USERNAME which specifies the current users username
            // TODO: Actually, no, LOGGED_IN_USER should be a User object, not a String
            DefaultUserService defaultUserService = new DefaultUserService();
            String username = defaultUserService.getUserByID(LOGGED_IN_USER).getUsername();
            newTask.setRequestingUserUsername(username);

            // TODO: Testing
            if (latLng != null) {
                newTask.setLocation(latLng.latitude, latLng.longitude, address);
            }

            newTask.setPhotoGallery(this.gallery);

            service.post(newTask);
            Toast postTask = Toast.makeText(this, "Task added", Toast.LENGTH_SHORT);
            postTask.show();

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast errorToast = Toast.makeText(this, "Task failed to be posted", Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }

    private void updateTask(String title, String desc) {

        try {
            editTask.setTitle(title);
            editTask.setDescription(desc);{
                // TODO: BUG: Can't update, it just crashes on this line
            editTask.setLocation(latLng.latitude, latLng.longitude, address);
            service.update(editTask);
            Toast postTask = Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT);
            postTask.show();

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Log.e("Update task", e.toString());
            Toast errorToast = Toast.makeText(this, "Task failed to update", Toast.LENGTH_SHORT);
            errorToast.show();
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

    private void addNewImage(int photoIndex) {
        if (photoIndex < 0 || photoIndex >= PhotoGallery.MAX_PHOTOS)
            throw new IllegalArgumentException("Index out of range");

        // Send intent to get image
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_REQUEST + photoIndex);
    }

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

            // Source: https://stackoverflow.com/questions/38352148
            Uri selectedImage = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                if (image == null) throw new IOException("Image failed to load");
                // Shrink the bitmap so it fits in the database nicely
                image = PhotoGallery.shrinkBitmap(image);
                gallery.insert(image, requestCode - PHOTO_REQUEST);
                refreshImages();
            } catch (IOException e) {
                Log.i("AddEditTaskActivity", "Failed to load image");
                Toast.makeText(this, "Image could not be loaded", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
