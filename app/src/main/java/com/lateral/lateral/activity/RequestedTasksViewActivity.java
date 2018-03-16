package com.lateral.lateral.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.ItemClickSupport;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.activity.TaskViewActivity.EXTRA_TASK_ID;


public class RequestedTasksViewActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;
    private String thisUserID = "AWIjITT14-hmkvYTCt4N";          // for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_tasks_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        matchingTasks = new ArrayList<Task>();

        // defining recycler view
        mRecyclerView = findViewById(R.id.requestedTaskViewList);
        mRecyclerView.setHasFixedSize(true);

        ItemClickSupport.addTo(mRecyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("ITEM CLICKED", "AN ITEM WAS CLICKED");
                        Intent viewTaskIntent = new Intent(RequestedTasksViewActivity.this, MyTaskViewActivity.class);
                        viewTaskIntent.putExtra(EXTRA_TASK_ID, (matchingTasks.get(position).getId()));
                        startActivity(viewTaskIntent);
                    }
                });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskRowAdapter(matchingTasks);
        mRecyclerView.setAdapter(mAdapter);

        // display requested tasks from this user
        returnMatchingTasks(thisUserID);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //SearchView searchView;

        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.task_view_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    private void addTasks(ArrayList<Task> returnedTasks) {
        matchingTasks.addAll(returnedTasks);
        mAdapter.notifyItemInserted(matchingTasks.size() - 1);

    }

    private void clearList() {
        final int size = matchingTasks.size();
        matchingTasks.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
    }

    private void returnMatchingTasks(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasksByRequesterID(query));
    }


}
