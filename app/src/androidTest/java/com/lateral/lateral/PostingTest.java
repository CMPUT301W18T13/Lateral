package com.lateral.lateral;

import android.support.test.runner.AndroidJUnit4;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Date;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PostingTest {


    @Test
    public void testPostTask() {
        /*
        DefaultTaskService defaultTaskService = new DefaultTaskService();
        Task testTask = new Task("Garbage Pickup", "Picking up roadside garbage, $15/h");
        defaultTaskService.post(testTask);
        testTask = new Task("Snow Removal", "Would like someone to help shovel my driveway");
        defaultTaskService.post(testTask);
        testTask = new Task("Babysitter Wanted", "URGENT: need a babysitter for tonight");
        defaultTaskService.post(testTask);
        testTask = new Task("Math Tutor needed", "I need someone to teach me math");
        defaultTaskService.post(testTask);
        */
    }

    @Test
    public void testPostBid() {

    }

    @Test
    public void testCreateUser() {
        //User testUser = new User("tlaz", "555-555-5555", "email@gmail.ca", "idkl");
        //DefaultUserService defaultUserService = new DefaultUserService();
        //defaultUserService.post(testUser);

    }
}
