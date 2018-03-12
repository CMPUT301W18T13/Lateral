package com.lateral.lateral;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.SearchService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
// TODO: instantiating Search service is a problem
public class SearchServiceTest {

    @Test
    public void testInstantiation(){
        SearchService testSS = new SearchService();
    }


    @Test
    public void testGetTaskById() {
        SearchService testSS = new SearchService();
        Task task = testSS.getTaskById("AWH0KdC_5ShdDfA-iIUG");

        assertEquals(task.getId(), "AWH0KdC_5ShdDfA-iIUG");

    }

    @Test
    public void testGetTaskByTitle(){
        SearchService testSS = new SearchService();

        String json = "{\"query\": {\"match\": {\"title\": {\"query\" : \"Shoveling\"}}}}";
        String returningData;
        Gson gson = new Gson();
        returningData = testSS.get(json);
        Task task = gson.fromJson(returningData, Task.class);
        assertEquals(task.getTitle(), "Shoveling");

    }

    @Test
    public void testSearch() {
        SearchService testSS = new SearchService();
        Task task = testSS.Search("Shoveling sucks");
        assertEquals(task.getTitle(), "Shoveling");
    }


}
