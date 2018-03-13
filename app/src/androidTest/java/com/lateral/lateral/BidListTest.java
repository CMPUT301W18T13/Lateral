package com.lateral.lateral;

/* Need updating considering new changes with user
import android.support.test.runner.AndroidJUnit4;

import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.BidList;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BidListTest {
    private static User user = new User ("John", "7805769876",
            "John@gmail.com");
    private static Task task = new Task("Drive me");
    private static BigDecimal testAmount = new BigDecimal(8.2);

    @Test
    public void testAdd() {

        BidList bids = new BidList();
        Bid bid = new Bid(testAmount, user , task);
        bid.setId("MDM");

        assertFalse(bids.has(bid));
        bids.add(bid);
        assertTrue(bids.has(bid));
    }

    @Test
    public void testHas() {
        BidList bids = new BidList();
        Bid bid = new Bid(testAmount, user , task);
        bid.setId("MDM");

        assertFalse(bids.has(bid));
        bids.add(bid);
        assertTrue(bids.has(bid));
    }


    @Test
    public void testGetCount(){
        BidList bids = new BidList();
        assertEquals(0, bids.getCount());
        Bid bid = new Bid(testAmount, user , task);
        bid.setId("MNM");
        bids.add(bid);
        bid = new Bid(testAmount, user , task);
        bid.setId("ABC");
        bids.add(bid);
        bid = new Bid(testAmount, user , task);
        bid.setId("KDH");
        bids.add(bid);
        assertEquals(3, bids.getCount());
    }

    @Test
    public void testGet() {
        // using index
        BidList bids = new BidList();
        Bid bid = new Bid(testAmount, user , task);
        bid.setId("MDM");

        bids.add(bid);
        Bid returnedBid = bids.get(bid);
        assertEquals(returnedBid.getId(), bid.getId());
    }

    @Test
    public void testGetAll() {
        ArrayList<Bid> bidArray;
        BidList bids = new BidList();
        Bid bid = new Bid(testAmount, user , task);
        bid.setId("MNM");
        bids.add(bid);
        bid = new Bid(testAmount, user , task);
        bid.setId("ABC");
        bids.add(bid);
        bid = new Bid(testAmount, user , task);
        bid.setId("KDH");
        bids.add(bid);
        bidArray = bids.getAll();
        assertEquals(3, bidArray.size());
    }

    @Test
    public void testDelete(){

        BidList bids = new BidList();
        Bid bid = new Bid(testAmount, user , task);
        bid.setId("MDM");

        bids.add(bid);
        bids.delete(bid);
        assertFalse(bids.has(bid));

    }

}
*/
