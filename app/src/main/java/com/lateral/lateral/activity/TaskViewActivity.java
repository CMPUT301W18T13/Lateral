package com.lateral.lateral.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lateral.lateral.dialog.BidDialog;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
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

public class TaskViewActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lateral.lateral.TASK_ID";

    private String taskID;

    private Task task;
    private BidService bidService = new DefaultBidService();
    private TaskService taskService = new DefaultTaskService();

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
        //Log.d("TASKVIEWACTIVITY", "id = " + this.taskID);
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

        task = loadTask();
        if (task.getLowestBid() == null){
            //Editor complains unless I save as string then setText
            String noBidsString = "No Bids";
            currentBid.setText(noBidsString);
        } else {
            currentBid.setText(getString(R.string.dollar_amount_display,
                    String.valueOf(task.getLowestBid().getAmount())));
        }
        title.setText(task.getTitle());
        username.setText(task.getRequestingUser().getUsername());
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        date.setText(df.format(task.getDate()));
        description.setText(task.getDescription());

    }

    private Task loadTask(){

        UserService userService = new DefaultUserService();

        Task task = taskService.getById(taskID);
        task.setRequestingUser(userService.getById(task.getRequestingUserId()));
        task.setLowestBid(bidService.getLowestBid(task.getId()));
        // TODO: Handle null task
        return task;
    }

    public void onBidButtonClick(View v){
        final BidDialog bidCreationDialog=new BidDialog(TaskViewActivity.this);
        bidCreationDialog.setCanceledOnTouchOutside(false);
        bidCreationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Bid newBid = bidCreationDialog.getNewBid();
                if (newBid != null){
                    newBid.setTaskId(taskID);
                    newBid.setBidderId(task.getAssignedUserId()); // TODO: check if this is correct
                    task.addBid(newBid);

                    if (task.getLowestBid() == null){
                        task.setLowestBid(newBid);
                        currentBid.setText(getString(R.string.dollar_amount_display,
                                String.valueOf(newBid.getAmount())));
                    } else if (newBid.getAmount().compareTo(task.getLowestBid().getAmount()) < 0){
                        task.setLowestBid(newBid);
                        currentBid.setText(getString(R.string.dollar_amount_display,
                                String.valueOf(newBid.getAmount())));
                    }

                    bidService.post(newBid);// Make sure bid has task Id
                }
            }
        });
        bidCreationDialog.show();
    }
}
