package com.lateral.lateral.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
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
import java.util.Locale;

public class MyTaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private String taskID;

    // UI elements
    private TextView currentBid;
    private TextView title;
    private TextView date;
    private TextView description;
    private TextView assignedToUsername;

    TaskService taskService = new DefaultTaskService();
    UserService userService = new DefaultUserService();
    BidService bidService = new DefaultBidService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task_view);

        // Get the task id
        Intent taskIntent = getIntent();
        this.taskID = taskIntent.getStringExtra(EXTRA_TASK_ID);
        Log.d("MYTASKVIEWACTIVITY", "id = " + this.taskID);
        // TODO: Handle null id

        currentBid = findViewById(R.id.my_task_view_current_bid);
        title = findViewById(R.id.my_task_view_title);
        date = findViewById(R.id.my_task_view_date);
        description = findViewById(R.id.my_task_view_description);
        assignedToUsername = findViewById(R.id.my_text_view_username);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Task task = loadTask(taskID);
//        BigDecimal lowest = task.getLowestBid().getAmount();
//        currentBid.setText(NumberFormat.getCurrencyInstance().format(lowest));
        title.setText(task.getTitle());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());
        User assignedUser = task.getAssignedUser();
        String username = (assignedUser == null) ? "" : assignedUser.getUsername();
        assignedToUsername.setText(username);
    }

    private Task loadTask(String taskID){
        Task task = taskService.getById(taskID);
        //task.setLowestBid(bidService.getLowestBid(task.getId()));
        task.setLowestBid(bidService.getLowestBid(task.getId()));
        if (task.getAssignedUserId() != null) {
            task.setAssignedUser(userService.getById(task.getAssignedUserId()));
        }
        return task;
        // TODO: Handle null task
    }

    public void onSeeBidButtonClick(View v){
        // TODO: View bids in new activity
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_edit_task){
            // Edit the task
            Intent intent = new Intent(this, AddEditTaskActivity.class);
            intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, taskID);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.action_delete_task){
            taskService.delete(taskID);
            // TODO: Navigate back
        }
        return super.onOptionsItemSelected(item);
    }

}
