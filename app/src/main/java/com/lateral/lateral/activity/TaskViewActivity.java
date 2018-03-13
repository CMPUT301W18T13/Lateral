package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class TaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private String taskID;

    // UI elements
    private TextView currentBid;
    private TextView title;
    private TextView username;
    private TextView date;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Get the task id
        Intent taskIntent = getIntent();
        this.taskID = taskIntent.getStringExtra(EXTRA_TASK_ID);
        // TODO: Handle null id

        currentBid = findViewById(R.id.task_view_current_bid);
        title = findViewById(R.id.task_view_title);
        username = findViewById(R.id.task_view_username);
        date = findViewById(R.id.task_view_date);
        description = findViewById(R.id.task_view_description);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Task task = loadTask(taskID);

        BigDecimal lowest = task.getLowestBid().getAmount();
        currentBid.setText(NumberFormat.getCurrencyInstance().format(lowest));
        title.setText(task.getTitle());
        username.setText(task.getRequestingUser().getUsername());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy");
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());

    }

    private Task loadTask(String taskID){
        TaskService taskService = new DefaultTaskService();
        UserService userService = new DefaultUserService();
        BidService bidService = new DefaultBidService();
        Task task = taskService.getById(taskID);
        task.setRequestingUser(userService.getById(task.getRequestingUserId()));
        task.setLowestBid(bidService.getLowestBid(task.getId()));
        return task;
    }

    public void onBidButtonClick(View v){
        // TODO: Place bid via dialog box
    }
}
