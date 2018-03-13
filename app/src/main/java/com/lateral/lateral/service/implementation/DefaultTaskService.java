package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.TaskService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
failure to return any results will always return null, so check accordingly
 */

public class DefaultTaskService extends DefaultBaseService<Task> implements TaskService {

    /*
     return task by title
      */
    public Task getTaskByTitle(String title){
        String json = "{\"query\": {\"match\": {\"title\": \"" + title + "\"}}}";
        return gson.fromJson(get(json), Task.class);
    }

    /*
     return a list of tasks matching the supplied query keywords
      */

    public ArrayList<Task> getAllTasks(String query){
        String json = "{\"query\": {\"match\": {\"title\": \"" + query + "\"}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + get(json) + "]", listType);
    }
}

