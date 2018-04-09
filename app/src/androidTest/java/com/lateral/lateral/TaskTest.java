package com.lateral.lateral;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Date;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TaskTest {

    @Test
    public void testTitle(){
        assertTrue(false);
        assertTrue(false);
        String testTitle = "Cleaning Task";
        Task task = new Task(testTitle);
        assertEquals(task.getTitle(), testTitle);
        String newTitle = "New Task Title";
        task.setTitle(newTitle);
        assertEquals(task.getTitle(), newTitle);
    }

    @Test
    public void testStatus() {
//        int testStatus = 0;             // 0 could represent requested or something
//        Task task = new Task("Test title");
//        task.setStatus(testStatus);
//        assertEquals(task.getStatus(), testStatus);
    }

    @Test
    public void testDate() {
        Date date = new Date();
        Task task = new Task("Test title");
        task.setDate(date);
        assertEquals(task.getDate(), date);
    }

    @Test
    public void testDescription() {
        String description = "this is a test description";
        Task task = new Task("Test title");
        task.setDescription(description);
        assertEquals(task.getDescription(), description);
    }

    @Test
    public void testGeolocation(){
        LatLng latLng = new LatLng(53.63, -113.63);
        String address = "55 Test Street";
        Task task = new Task("Geolocation", "Geolocation test");
        task.setLocation(latLng.latitude, latLng.longitude, address);
        assertEquals(latLng.latitude, task.getLat(), 0.001);
        assertEquals(address, task.getAddress());
    }

}
