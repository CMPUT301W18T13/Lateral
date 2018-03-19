package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lateral.lateral.Constants;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;

public class AddEditTaskActivity extends AppCompatActivity {

    // Pass null (or nothing) to add a new task, otherwise edit the given task
    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private String taskID;
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
        this.taskID = taskIntent.getStringExtra(EXTRA_TASK_ID);

        title = findViewById(R.id.add_edit_task_title);
        description = findViewById(R.id.add_edit_task_description);
        confirmButton = findViewById(R.id.add_edit_task_confirm_button);
        setInputFilters();

        service = new DefaultTaskService();
    }

    @Override
    protected void onStart(){
        super.onStart();

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
            try{
                editTask = service.getById(taskID);
            } catch (Exception e){
                Toast errorToast = Toast.makeText(this,"Task failed to load", Toast.LENGTH_SHORT);
                errorToast.show();
                setResult(RESULT_CANCELED);
                finish();
            }
            title.setText(editTask.getTitle());
            description.setText(editTask.getDescription());
        }
    }

    protected void setInputFilters(){
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

                if(s.toString().trim().length()==0) confirmButton.setEnabled(false);
                else confirmButton.setEnabled(true);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void onAddEditConfirmClick(View v){

        String title = this.title.getText().toString().trim();
        String desc = this.description.getText().toString().trim();

        if (editTask == null) addNewTask(title, desc);
        else updateTask(title, desc);
    }

    private void addNewTask(String title, String desc) {

        try {
            Task newTask = new Task(title, desc);
            newTask.setRequestingUserId(LOGGED_IN_USER);
            // TODO: Testing
            newTask.setLocation(53.644250, -113.652206);
            service.post(newTask);
            Toast postTask = Toast.makeText(this, "Task added", Toast.LENGTH_SHORT);
            postTask.show();

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast errorToast = Toast.makeText(this,"Task failed to be posted", Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }

    private void updateTask(String title, String desc) {

        try {
            editTask.setTitle(title);
            editTask.setDescription(desc);
            service.update(editTask);
            Toast postTask = Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT);
            postTask.show();

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast errorToast = Toast.makeText(this,"Task failed to update", Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }
}
