package com.lateral.lateral.service.implementation;

import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.TaskService;


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
}
