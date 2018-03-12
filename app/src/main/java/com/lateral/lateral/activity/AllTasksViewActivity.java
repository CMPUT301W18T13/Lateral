package com.lateral.lateral.activity;

import android.app.SearchManager;
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
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;
import java.util.List;

/*
Searching interface info
https://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget

Good resource for recycler view implementation
https://developer.android.com/guide/topics/ui/layout/recyclerview.html#Adapter

info on displaying search results in the current activity
https://developer.android.com/guide/topics/search/search-dialog.html#LifeCycle

 */

// TODO fix white bar at the top of this activity --> appeared on my original pull
// TODO cleanup to improve readablity (including unnecessary changes in other files)
public class AllTasksViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;

    List<Task> testTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check how we got here
        handleIntent(getIntent());

        // sample tasks
        testTasks = new ArrayList<Task>();


        // makes some example tasks to put into the recycler view
        String taskName;
        String taskDescription;
//        for (int i = 0; i < 20; i++) {
//            taskName = "Task " + i;
//            taskDescription = "this is task " + i;
//            testTasks.add(new Task(taskName, taskDescription));
//        }

        // Posts to our database
//        Task t = new Task("walk my dog", "I want you to talk my doggy");
//        DefaultTaskService taskService = new DefaultTaskService();
//        taskService.post(t);




        // Recycler view stuff
        mRecyclerView = (RecyclerView) findViewById(R.id.taskViewList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this, testTasks);
        mRecyclerView.setAdapter(mAdapter);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //setSupportActionBar(toolbar);
//                Snackbar.make(view, "Test", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_view_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconified(true);
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
            //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
            clearList();
            String query = intent.getStringExtra(SearchManager.QUERY);
            returnMatchingTask(query);

        }

    }

    private void addTasks(ArrayList<Task> returnedTasks) {
        //testTasks.add(task);
        testTasks.addAll(returnedTasks);
        mAdapter.notifyItemInserted(testTasks.size() - 1);

    }

    private void clearList() {
        final int size = testTasks.size();
        testTasks.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
    }

    // TODO make sure you dont add empty results --> what does a search with no hits return?
    private void returnMatchingTask(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasks(query));
//        ArrayList<Task> returnedTasks = taskService.getAllTasks(query);
//        testTasks.addAll(returnedTasks);
//        mAdapter.notifyItemInserted(testTasks.size() - 1);
        Log.d("size", "" + testTasks.size());
    }


}
