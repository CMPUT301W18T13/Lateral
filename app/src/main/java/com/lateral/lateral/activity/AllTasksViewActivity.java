package com.lateral.lateral.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.lateral.lateral.R;
import com.lateral.lateral.service.implementation.DefaultTaskService;

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
        super.onCreate(savedInstanceState);
        // check how we got here
        handleIntent(getIntent());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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

    private void returnMatchingTask(String query) {
        DefaultTaskService taskService = new DefaultTaskService();
        addTasks(taskService.getAllTasks(query));
    }


}
