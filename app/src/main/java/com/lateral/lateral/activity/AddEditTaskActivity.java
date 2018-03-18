package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lateral.lateral.Constants;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;

public class AddEditTaskActivity extends AppCompatActivity {

    // Pass null (or nothing) to add a new task, otherwise edit the given task
    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private Task editTask;
    private TaskService service;

    // UI elements
    private EditText title;
    private EditText description;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Get the task id
        Intent taskIntent = getIntent();
        String taskID = taskIntent.getStringExtra(EXTRA_TASK_ID);

        title = findViewById(R.id.add_edit_task_title);
        description = findViewById(R.id.add_edit_task_description);
        confirmButton = findViewById(R.id.add_edit_task_confirm_button);
        setInputFilters();

        service = new DefaultTaskService();

        if (taskID == null){
            // Add new task
            confirmButton.setText(R.string.add_task_confirm);
            setTitle(R.string.add_task_title);
            editTask = null;
        }
        else{
            // Edit existing task
            confirmButton.setText(R.string.edit_task_confirm);
            setTitle(R.string.edit_task_title);
            editTask = service.getById(taskID);
            // TODO: Handle null task
        }
    }

    protected void setInputFilters(){
        // TODO: Test that these actually work
        InputFilter[] titleFilter = new InputFilter[1];
        titleFilter[0] = new InputFilter.LengthFilter(Constants.TITLE_CHAR_LIMIT);
        title.setFilters(titleFilter);

        InputFilter[] descFilter = new InputFilter[1];
        descFilter[0] = new InputFilter.LengthFilter(Constants.DESCRIPTION_CHAR_LIMIT);
        title.setFilters(descFilter);
        // TODO: Disable button while title is empty using TextWatcher
    }

    public void onAddEditConfirmClick(View v){
        try{
            // TODO: Trim the title and desc??
            String title = this.title.getText().toString();
            String desc = this.description.getText().toString();

            if (editTask == null){
                // I have posted an object with lat and long, posted by user set in LOGGED_IN_USER
                // can be changed to reflect design
                Task newTask = new Task(title, desc);
                newTask.setRequestingUserId(LOGGED_IN_USER);
                newTask.setLocation(53.644250, -113.652206);
                service.post(newTask);
            }
            else{
                editTask.setTitle(title);
                editTask.setDescription(desc);
                // TODO: Need to call service.update()
            }

            // TODO: Navigate back on success
        }
        catch (Exception ex){
            // TODO: Handle errors!
        }
    }
}
