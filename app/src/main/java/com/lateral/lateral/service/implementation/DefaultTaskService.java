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


public class DefaultTaskService extends DefaultBaseService<Task> {
    // TODO: Add extra methods specific to the Task index

    public Task getTaskByTitle(String title){
        String json = "{\"query\": {\"match\": {\"title\": \"" + title + "\"}}}";
        return gson.fromJson(get(json), Task.class);
    }

    public Task getTaskById(String id){
        String json = "{\"query\": {\"match\": {\"_id\": \"" + id + "\"}}}";
        return gson.fromJson(get(json), Task.class);
    }

    public ArrayList<Task> getAllTasks(String query){
        String json = "{\"query\": {\"match\": {\"title\": \"" + query + "\"}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + get(json) + "]", listType);
    }
}