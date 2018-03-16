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
import android.view.View;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.ItemClickSupport;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.util.ArrayList;

import static com.lateral.lateral.activity.TaskViewActivity.EXTRA_TASK_ID;

/*
Searching interface info
https://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget

Good resource for recycler view implementation
https://developer.android.com/guide/topics/ui/layout/recyclerview.html#Adapter

info on displaying search results in the current activity
https://developer.android.com/guide/topics/search/search-dialog.html#LifeCycle

 */

// TODO fix white bar at the top of this activity --> appeared on my original pull
// TODO clicking seems to work but test more --> pass intents
public class AllTasksViewActivity extends TaskRecyclerViewActivity {
//
//    private RecyclerView.Adapter mAdapter;
//    private ArrayList<Task> matchingTasks;

    @Override
    protected int getResourceLayoutID(){
        return R.layout.activity_all_tasks_view;
    }

    @Override
    protected int getRecyclerListID() {
        return R.id.taskViewList;
    }

    @Override
    protected Context currentActivityContext(){
        return AllTasksViewActivity.this;
    }

    @Override
    protected Class targetClass() {
        return TaskViewActivity.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        RecyclerView mRecyclerView;
//        RecyclerView.LayoutManager mLayoutManager;

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_all_tasks_view);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        matchingTasks = new ArrayList<Task>();
//
//        // defining recycler view
//        mRecyclerView = findViewById(R.id.taskViewList);
//        mRecyclerView.setHasFixedSize(true);
//
//        ItemClickSupport.addTo(mRecyclerView)
//                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                        Log.d("ITEM CLICKED", "item " + (matchingTasks.get(position).getId()));
//                        Intent viewTaskIntent = new Intent(AllTasksViewActivity.this, TaskViewActivity.class);
//                        viewTaskIntent.putExtra(EXTRA_TASK_ID, (matchingTasks.get(position).getId()));
//                        startActivity(viewTaskIntent);
//                    }
//                });
//
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new TaskRowAdapter(matchingTasks);
//        mRecyclerView.setAdapter(mAdapter);
//
        // check how we got here
        handleIntent(getIntent());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//
//        SearchView searchView;
//
//        // Inflate the options menu from XML
//        getMenuInflater().inflate(R.menu.task_view_menu, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        // Assumes current activity is the searchable activity
//        // (although need this line in each activity for some reason)
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        //searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        return true;
    }


    //@Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    // meat and potatoes of the search
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            clearList();
            String query = intent.getStringExtra(SearchManager.QUERY);
            // TODO handle exception (no internet access crashes app)
            /*
            Leave for now
            String jsonQuery = "{\"query\": {\"multi_match\": {\"title\": {\"query\" : \"" + searchField + "\"," +
                " \"fields\" : [\"title^3\", \"description\"]}}}}";
             */
            returnMatchingTask(query);
        }

    }

//    @Override
//    public void addTasks(ArrayList<Task> returnedTasks) {
//        super.addTasks(returnedTasks);
////        matchingTasks.addAll(returnedTasks);
////        mAdapter.notifyItemInserted(matchingTasks.size() - 1);
//
//    }

//    public void clearList() {
//        super.clearList();
////        final int size = matchingTasks.size();
////        matchingTasks.clear();
////        mAdapter.notifyItemRangeRemoved(0, size);
//    }

    private void returnMatchingTask(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasks(query));
    }


}
