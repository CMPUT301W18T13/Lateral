package com.lateral.lateral;

import android.support.test.runner.AndroidJUnit4;
import com.lateral.lateral.model.Bid;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BidTest {

    @Test
    public void testBidConstructor(){
        try {
            new Bid(8.2f, "John", "Drive Me");
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        try {
            new Bid(0.0f, "T", "D");
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

        try {
            new Bid(598671, "Uvuvwevw", "Ossas");
            assertTrue(Boolean.TRUE);
        } catch (Exception e){
            assertTrue(Boolean.FALSE);
            e.getClass();
        }

    }

    @Test
    public void testNegativeAmount(){
        try {
            new Bid(-1, "John", "Drive Me");
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testEmptyUsername(){
        try {
            new Bid(8.2f, "", "Drive Me");
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testEmptyTaskName(){
        try {
            new Bid(8.2f, "John", "");
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void  testGetters(){
        Bid bid = new Bid(8.2f, "John", "Drive me");
        assertEquals(Constants.BID_POSTED, bid.getStatus());
        assertEquals(8.2f, bid.getAmount(), 0.01);
        assertEquals("John", bid.getBidder());
        assertEquals("Drive me", bid.getTaskBidOn());
    }

    @Test
    public void testStatusChange(){
        Bid bid = new Bid(8.2f, "John", "Drive me");
        bid.setStatus(Constants.BID_ACCEPTED);
        assertEquals(Constants.BID_ACCEPTED, bid.getStatus());

        try {
            bid.setStatus(Constants.BID_POSTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            bid.setStatus(Constants.BID_DECLINED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        bid = new Bid(8.2f, "John", "Drive me");
        bid.setStatus(Constants.BID_DECLINED);
        assertEquals(Constants.BID_DECLINED, bid.getStatus());

        try {
            bid.setStatus(Constants.BID_POSTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            bid.setStatus(Constants.BID_ACCEPTED);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
}
