/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.lateral.lateral.activity.LoginActivity;
import com.lateral.lateral.activity.MainActivity;
import com.lateral.lateral.activity.TaskViewActivity;
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
import java.util.concurrent.TimeUnit;

import static com.lateral.lateral.Constants.USER_FILE_NAME;
import static com.lateral.lateral.service.UserLoginService.hashPassword;
import static com.lateral.lateral.service.UserLoginService.randomBytes;

@RunWith(AndroidJUnit4.class)
public class BidTest extends ActivityInstrumentationTestCase2<LoginActivity> {
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

    public BidTest(){
        super(LoginActivity.class);
    }

    private void create_test_information(){
        String salt = randomBytes(64);
        String saltAndHash = salt + ':' + hashPassword("malcolmT", salt);
        testUser = new User("malcolmT", "7803851748",
                "mimacart@ualberta.ca", saltAndHash);
        //testUser = userService.getUserByUsername("malcolm2");
        testTask = new Task("Test Title", "this is a test description");
        testAmount = new BigDecimal(8.2).setScale(2, RoundingMode.CEILING);
    }

    @Test
    public void testBidConstructor(){
        create_test_information();

        //Create a new bid 3 arguments
        try {
            new Bid(testAmount, testUser.getId() , testTask.getId());
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        //Make sure 0 is a valid argument when creating a new bid with 3 arguments
        try {
            new Bid(new BigDecimal(00.00), testUser.getId() , testTask.getId());
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        //Create a new bid one arguments
        try {
            new Bid(testAmount);
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        //Make sure 0 is a valid argument when creating a new bid
        try {
            new Bid(new BigDecimal(00.00));
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

    }

    @Test
    public void testInvalidConstructorArgs(){
        create_test_information();

        //Pass a negative amount
        try {
            new Bid(new BigDecimal(-1) , testUser.getId(), testTask.getId());
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        try {
            new Bid(new BigDecimal(-1));
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testGetters(){
        create_test_information();

        try {
            userService.post(testUser);
            taskService.post(testTask);
        } catch (Exception e){
            assertTrue(false);
        }
        Bid bid = new Bid(testAmount, testTask.getId(),testUser.getId());

        if (testAmount.compareTo(bid.getAmount()) == 0){
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        int test = testUser.getId().compareTo(bid.getBidderId());
        if (testUser.getId().compareTo(bid.getBidderId()) == 0){
            try {
                userService.delete(testUser.getId());
            } catch (Exception e){
                assertTrue(false);
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        if (testTask.getId().compareTo(bid.getTaskId()) == 0) {
            try {
            taskService.delete(testTask.getId());
            } catch (Exception e){
                assertTrue(false);
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
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

    @Test
    public void testCreatingBid() {
        try {
            setUp();
            create_test_information();
        } catch (Exception e){
            Log.i("Error", "Error in solo creation");
            assertTrue(false);
        }

        try {
            userService.post(testUser);
            testTask.setRequestingUserId(testUser.getId());
            testTask.setRequestingUser(testUser);
            taskService.post(testTask);
        } catch (Exception e){
            assertTrue(false);
        }

        login(); //Fails on connection error
        //TODO change vvv
        //solo.clickOnText("Search Available Tasks");
        //solo.assertCurrentActivity("Available Tasks View Activity",
        //        AvailableTasksViewActivity.class);

        //solo.enterText(0, "Smuggle");
        //wait(10);

        solo.clickOnText("Available Tasks");
        wait(1);

        solo.clickOnText("Test Title");

        //TODO change ^^^

        //Test bidding for first time
        solo.assertCurrentActivity("My Task View Activity", TaskViewActivity.class);
        solo.clickOnText("Bid Now!");
        solo.clickOnText("00");
        solo.enterText(0 ,"23.55" );
        solo.clickOnText("BID");
        wait(1);
        try {
            testBid = bidService.getLowestBid(testTask.getId());
            testTask = taskService.getById(testTask.getId());
        } catch (Exception e){
            assertTrue(false);
        }
        if (testBid.getAmount().compareTo(
                new BigDecimal("23.55").setScale(2, RoundingMode.CEILING)) == 0){
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        //Test overwriting
        solo.assertCurrentActivity("My Task View Activity", TaskViewActivity.class);
        solo.clickOnText("Bid Now!");
        solo.clickOnText("00");
        solo.enterText(0 ,"00.00" );
        solo.clickOnText("BID");
        wait(1);
        try {
            testBid = bidService.getLowestBid(testTask.getId());
            testTask = taskService.getById(testTask.getId());
        } catch (Exception e){
            assertTrue(false);
        }
        if (testBid.getAmount().compareTo(new BigDecimal("00.00")
                .setScale(2, RoundingMode.CEILING)) == 0){
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        try {
            userService.delete(testUser.getId());
            taskService.delete(testTask.getId());
        } catch (Exception e){
            assertTrue(false);
        }
    }
}



