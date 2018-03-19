package com.lateral.lateral;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.lateral.lateral.model.Bid;
//                         import com.lateral.lateral.model.BidStatus;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.robotium.solo.Solo;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BidTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public BidTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        solo = new Solo(getInstrumentation(), getActivity());
    }

    private void wait(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void testActivity() {
        // robotium assert
        //BidTest.setUp();
        //getInstrumentation();
        //getActivity();
        //solo = new Solo(getInstrumentation(), getActivity());
        try {
            setUp();
        } catch (Exception e){
            Log.i("Error", "Error in solo creation");
            assertTrue(false);
        }
        solo.assertCurrentActivity("Main Activity", MainActivity.class);
        wait(1);

        solo.clickOnText("RequestedTasksViewActivity");
        // junit assert
        assertEquals(true, true);
    }
}

/*@RunWith(AndroidJUnit4.class)
public class BidTest extends ActivityInstrumentationTestCase2<MainActivity> {
    //private static User user = new User ("John", "7805769876",
    //        "John@gmail.com");
    //private static Task task = new Task("Drive me");
    //private static BigDecimal testAmount = new BigDecimal(8.2);
    private Solo solo;

    @Test
    public void Hello() {
        solo = new Solo(getInstrumentation(), );
        solo.clickOnText("REQUESTEDTASKVIEWACTIVITY");
    }

    /*
    @Test
    public void testBidConstructor(){

        try {
            new Bid(testAmount, user , task);
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        try {
            new Bid(new BigDecimal(0.0), user, task);
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        try {
            new Bid(new BigDecimal(598671), user, task);
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

    }

    @Test
    public void testNegativeAmount(){
        try {
            new Bid(new BigDecimal(-1) , user, task);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testEmptyUsername(){
        try {
            new Bid(testAmount, null, task);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testEmptyTaskName(){
        try {
            new Bid(testAmount, user, null);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void  testGetters(){
        Bid bid = new Bid(testAmount, user, task);
        assertEquals(BidStatus.POSTED, bid.getStatus());
        assertEquals(testAmount, bid.getAmount());
        assertEquals(user, bid.getBidder());
        assertEquals(task, bid.getTaskBidOn());
    }

    @Test
    public void testStatusChange(){
        Bid bid = new Bid(testAmount, user, task);
        bid.setStatus(BidStatus.ACCEPTED);
        assertEquals(BidStatus.ACCEPTED, bid.getStatus());

        try {
            bid.setStatus(BidStatus.POSTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            bid.setStatus(BidStatus.REJECTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        bid = new Bid(testAmount, user, task);
        bid.setStatus(BidStatus.REJECTED);
        assertEquals(BidStatus.REJECTED, bid.getStatus());

        try {
            bid.setStatus(BidStatus.POSTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            bid.setStatus(BidStatus.ACCEPTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }*/
//}

