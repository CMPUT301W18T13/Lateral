package com.lateral.lateral.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.MainActivity.LOGGED_IN_USER;

public class RequestedTasksViewActivity extends TaskRecyclerViewActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;

    private String thisUserID = LOGGED_IN_USER;             // class spec       // for testing

    @Override
    protected int getResourceLayoutID(){
        return R.layout.activity_requested_tasks_view;
    }

    @Override
    protected int getRecyclerListID() {
        return R.id.requestedTaskViewList;
    }

    @Override
    protected Context currentActivityContext(){
        return RequestedTasksViewActivity.this;
    }

    @Override
    protected Class targetClass() {
        return MyTaskViewActivity.class;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Move to create new task activity when fab pressed
        FloatingActionButton addNewTaskFab = (FloatingActionButton) findViewById(R.id.addNewTaskFab);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddTaskIntent = new Intent(RequestedTasksViewActivity.this, AddEditTaskActivity.class);
                startActivity(AddTaskIntent);
            }
        });

        // display requested tasks from this user
        returnMatchingTasks(thisUserID);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private void returnMatchingTasks(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasksByRequesterID(query));
    }


}
