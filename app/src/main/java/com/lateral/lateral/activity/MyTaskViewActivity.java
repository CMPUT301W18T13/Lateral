/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.Activity;
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
import com.lateral.lateral.model.BidEvent;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

// Malcolm's test task id: AWI9EJpYAJsZenWtuKsd

import static com.lateral.lateral.model.BidEvent.*;

public class MyTaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";
    public static final String BID_EVENT = "com.lateral.lateral.BID_EVENT";
    public static final String ACCEPTED_BID_ID = "com.lateral.lateral.ACCEPTED_BID_ID";

    private String taskID;

    private Task task;

    // UI elements
    private TextView currentBid;
    private TextView title;
    private TextView date;
    private TextView description;
    private TextView assignedToUsername;

    private TaskService taskService = new DefaultTaskService();
    private UserService userService = new DefaultUserService();
    private BidService bidService = new DefaultBidService();

    /**
     * Called when the activity is created
     * @param savedInstanceState the saved instance
     */
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

    /**
     * Called when the activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();

        task = loadTask(taskID);

        if (task.getLowestBid() == null){
            //Editor complains unless I save as string then setText
            String noBidsString = "No Bids";
            currentBid.setText(noBidsString);
        } else {
            currentBid.setText(getString(R.string.dollar_amount_display,
                    String.valueOf(task.getLowestBid().getAmount())));
        }
        title.setText(task.getTitle());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());
        User assignedUser = task.getAssignedUser();
        String username = (assignedUser == null) ? "" : assignedUser.getUsername();
        assignedToUsername.setText(username);
    }

    /**
     * Loads a task based on its taskID
     * @param taskID Task ID of the task
     * @return The loaded task
     */
    private Task loadTask(String taskID){
        Task task = taskService.getById(taskID);
        task.setLowestBid(bidService.getLowestBid(task.getId()));
        if (task.getAssignedUserId() != null) {
            task.setAssignedUser(userService.getById(task.getAssignedUserId()));
        }
        return task;
        // TODO: Handle null task
    }

    /**
     * Called when see bid button is clicked
     * @param v The current view
     */
    public void onSeeBidButtonClick(View v){
        Intent intent = new Intent(this, BidListActivity.class);
        intent.putExtra(BidListActivity.TASK_ID, taskID);
        startActivityForResult(intent, 1);
    }

    /**
     * Called when the options menu is created
     * @param menu The menu created
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_task_menu, menu);
        return true;
    }

    /**
     * Called when an options menu item is selected
     * @param item The menu item selected
     * @return The built in result of calling onOptionsItemSelected
     */
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

    /**
     * Called when the child activity exits
     * @param requestCode The request code supplied by calling function
     * @param resultCode The result code returned by the child activity
     * @param data The Intent supplied
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK ){
            BidEvent bidEvent = (BidEvent) data.getSerializableExtra(BID_EVENT);
            if (bidEvent == BID_DECLINED) {
                //Check if the lowest bid has been declined
                task.setLowestBid(bidService.getLowestBid(task.getId()));
                if (task.getLowestBid() == null){
                    //Editor complains unless I save as string then setText
                    String noBidsString = "No Bids";
                    currentBid.setText(noBidsString);
                } else {
                    currentBid.setText(getString(R.string.dollar_amount_display,
                            String.valueOf(task.getLowestBid().getAmount())));
                }
                // TODO: handle null value
            } else if (bidEvent == BID_ACCEPTED){
                String bidID = data.getStringExtra(ACCEPTED_BID_ID);
                //Tyler TODO: getBidByBidId(String bidID);
                //Devon TODO: handle that a bid has been accepted ---------------------------------------------
            }

        }
    }

}
