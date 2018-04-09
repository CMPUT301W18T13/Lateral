/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.lateral.lateral.activity.AddEditTaskActivity;
import com.lateral.lateral.activity.LoginActivity;
import com.lateral.lateral.activity.MainActivity;
import com.lateral.lateral.activity.RequestedTasksViewActivity;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;
import com.lateral.lateral.service.implementation.DefaultUserService;
import com.robotium.solo.Solo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.model.TaskStatus.Assigned;
import static com.lateral.lateral.model.TaskStatus.Bidded;
import static com.lateral.lateral.model.TaskStatus.Done;
import static com.lateral.lateral.model.TaskStatus.Requested;
import static com.lateral.lateral.service.UserLoginService.hashPassword;
import static com.lateral.lateral.service.UserLoginService.randomBytes;

@RunWith(AndroidJUnit4.class)
public class TaskTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private static User testUser = null;
    private static Task testTask = null;
    private static Bid testBid = null;
    private static BigDecimal testAmount;
    private static UserService userService = new DefaultUserService();
    private static TaskService taskService = new DefaultTaskService();
    private static BidService bidService = new DefaultBidService();
    private static Solo solo;

    @Rule
    public SimpleOnFailed ruleExample = new SimpleOnFailed();

    private class SimpleOnFailed extends TestWatcher {
        @Override
        protected void failed(Throwable e, Description description) {
            try {
                if (testUser != null) {
                    if (testUser.getId() != null) {
                        userService.delete(testUser.getId());
                    }
                }

                if (testTask != null) {
                    if (testTask.getId() != null) {
                        taskService.delete(testTask.getId());
                    }
                }

                if (testBid != null) {
                    if (testBid.getId() != null) {
                        bidService.delete(testTask.getId());
                    }
                }
            } catch (Exception er){
                Log.e("Error", "Failed to delete an object");
            }
        }
    }

    public TaskTest(){
        super(LoginActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getInstrumentation().getTargetContext().deleteFile(USER_FILE_NAME);
        solo = new Solo(getInstrumentation(), getActivity());
    }

    private void login(){
        solo.assertCurrentActivity("Login Activity", LoginActivity.class);

        EditText usernameEditText = (EditText) solo.getView(R.id.username);
        EditText passwordEditText = (EditText) solo.getView(R.id.password);

        solo.enterText(usernameEditText, "malcolm1"); //assuming this account exists with
        solo.enterText(passwordEditText, "malcolm1"); //                    this password

        solo.clickOnText("Sign in");
        solo.assertCurrentActivity("Main Activity", MainActivity.class);
    }


    private void wait(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (Exception e){
            assertTrue(false);
        }
    }

    private void create_test_information(){
        String salt = randomBytes(64);
        String saltAndHash = salt + ':' + hashPassword("malcolmT", salt);
        testUser = new User("malcolmT", "7803851748",
                "mimacart@ualberta.ca", saltAndHash);
        testTask = new Task("Smuggle Some DrugsT", "this is a test description");
        testAmount = new BigDecimal(8.2).setScale(2, RoundingMode.CEILING);
    }

    @Test
    public void addTask(){
        try {
            setUp();
            create_test_information();
        } catch (Exception e){
            Log.i("Error", "Error in solo creation");
            assertTrue(false);
        }

        try {
            userService.post(testUser);
        } catch (Exception e){
            assertTrue(false);
        }

        login();
        solo.clickOnText("My Requested Tasks");


        solo.assertCurrentActivity("RequestedTasksViewActivity", RequestedTasksViewActivity.class);
        Intent addTaskIntent = new Intent(getInstrumentation().getTargetContext(), AddEditTaskActivity.class);
        getInstrumentation().getTargetContext().startActivity(addTaskIntent);

        solo.assertCurrentActivity("Add Edit Task Activity", AddEditTaskActivity.class);
        //EditText usernameEditText = (EditText) solo.getView(R.id.username);
        //EditText passwordEditText = (EditText) solo.getView(R.id.password);

        try {
            userService.delete(testUser.getId());
        } catch (Exception e){
            assertTrue(false);
        }

    }

    @Test
    public void testTitle(){
        String testTitle = "Cleaning Task";
        Task task = new Task(testTitle);
        assertEquals(task.getTitle(), testTitle);
        String newTitle = "New Task Title";
        task.setTitle(newTitle);
        assertEquals(task.getTitle(), newTitle);
    }

    @Test
    public void testStatus() {
        Task task = new Task("Test title");
        task.setStatus(Requested);
        assertEquals(task.getStatus(), Requested);
        task.setStatus(Bidded);
        assertEquals(task.getStatus(), Bidded);
        task.setStatus(Assigned);
        assertEquals(task.getStatus(), Assigned);
        task.setStatus(Done);
        assertEquals(task.getStatus(), Done);
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
