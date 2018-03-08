package com.lateral.lateral.service.implementation;


import com.lateral.lateral.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.searchbox.core.SearchResult;

public class SearchService extends DefaultTaskService {
    //private ArrayList<String> searchKeys;
    public int testInt = 5;

    public SearchService() {
        //this.searchKeys = extractKeywords(searchKeyString);
    }

    /* Main search function */
    public void search(String searchString) {
        ArrayList<Task> matchingTasks = null;
        ArrayList<String> searchKeys = extractKeywords(searchString);

        //Task task = get




    }



    public ArrayList<String> extractKeywords(String searchKeyString) {
        //ArrayList<String> keys = new ArrayList<String>();

        String[] items = searchKeyString.split("\\s+");
        ArrayList<String> keys = new ArrayList<String>(Arrays.asList(items));
        return keys;
    }

}
