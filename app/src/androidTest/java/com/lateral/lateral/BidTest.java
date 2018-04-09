package com.lateral.lateral;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.lateral.lateral.activity.AvailableTasksViewActivity;
import com.lateral.lateral.activity.LoginActivity;
import com.lateral.lateral.activity.MainActivity;
import com.lateral.lateral.model.Bid;
//                         import com.lateral.lateral.model.BidStatus;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.UserService;
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
    private static BigDecimal testAmount;
    private static UserService userService = new DefaultUserService();
    private static TaskService taskService = new DefaultTaskService();
    private static Solo solo;

    @Rule
    public SimpleOnFailed ruleExample = new SimpleOnFailed();

    private class SimpleOnFailed extends TestWatcher {
        @Override
        protected void failed(Throwable e, Description description) {
            if (testUser != null) {
                userService.delete(testUser.getId());
            }

            if (testTask != null) {
                taskService.delete(testTask.getId());
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
        testTask = new Task("Smuggle some drugs", "Cross the the border into " +
                "america, and drop them off at a place I will tell you about");
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

        userService.post(testUser);
        taskService.post(testTask);
        Bid bid = new Bid(testAmount, testTask.getId(),testUser.getId());

        if (testAmount.compareTo(bid.getAmount()) == 0){
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        int test = testUser.getId().compareTo(bid.getBidderId());
        if (testUser.getId().compareTo(bid.getBidderId()) == 0){
            userService.delete(testUser.getId());
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        if (testTask.getId().compareTo(bid.getTaskId()) == 0) {
            taskService.delete(testTask.getId());
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

        userService.post(testUser);
        taskService.post(testTask);

        login(); //Fails on connection error
        solo.clickOnText("Search Available Tasks");
        solo.assertCurrentActivity("Available Tasks View Activity",
                AvailableTasksViewActivity.class);
        assertTrue(true);
    }
}



