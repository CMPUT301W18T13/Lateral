package com.lateral.lateral.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

/*
Searching interface info
https://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget

Good resource for recycler view implementation
https://developer.android.com/guide/topics/ui/layout/recyclerview.html#Adapter

info on displaying search results in the current activity
https://developer.android.com/guide/topics/search/search-dialog.html#LifeCycle

 */

// TODO fix white bar at the top of this activity --> appeared on my original pull
public class AllTasksViewActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private ArrayList<Task> matchingTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check how we got here
        handleIntent(getIntent());

        matchingTasks = new ArrayList<Task>();

        // defining recycler view
        mRecyclerView = findViewById(R.id.taskViewList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskRowAdapter(matchingTasks);
        mRecyclerView.setAdapter(mAdapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SearchView searchView;

        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.task_view_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }


    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    // meat and potatoes of the search
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();     //Toast for debugging
            clearList();
            String query = intent.getStringExtra(SearchManager.QUERY);
            // implement some sort of exception handler (no internet access crashes app)
            /*
            Leave for now
            String jsonQuery = "{\"query\": {\"multi_match\": {\"title\": {\"query\" : \"" + searchField + "\"," +
                " \"fields\" : [\"title^3\", \"description\"]}}}}";
             */
            returnMatchingTask(query);
            Log.d("Search Failed", "The search failed for some reason");

        }

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

    private void returnMatchingTask(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasks(query));
    }


}
