package com.lateral.lateral.service.implementation;


import com.lateral.lateral.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.searchbox.core.SearchResult;

public class SearchService extends DefaultTaskService {

    ArrayList<Task> searchResults;

    public SearchService() {

    }


    // for now just returns task but should return all matching tasks
    public Task Search(String searchField) {

        // simple query which matches based on complete words in title and description
        // TODO: modify to allow results from partial matches
        String jsonQuery = "{\"query\": {\"multi_match\": {\"title\": {\"query\" : \"" + searchField + "\"," +
                " \"fields\" : [\"title^3\", \"description\"]}}}}";

        Task task = gson.fromJson(get(jsonQuery), Task.class);;

        return task;
    }

}
