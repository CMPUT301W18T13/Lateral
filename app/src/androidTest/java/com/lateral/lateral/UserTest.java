/*
package com.lateral.lateral;

import android.support.test.runner.AndroidJUnit4;

import com.lateral.lateral.model.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    @Test
    public void testUsername(){
        String testName = "TestName";
        User user = new User(testName, "", "");
        assertEquals(user.getUsername(), testName);
        testName = "NewName";
        user.setUsername(testName);
        assertEquals(user.getUsername(), testName);
    }

    @Test
    public void testLongUserName(){
        try{
            String tooLong = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
            User user = new User(tooLong, "","");

        } catch (IllegalArgumentException e){
            return;
        }
        // Error should've been raised
        fail();
    }

    @Test
    public void testPhoneNumber(){
        String testNumber = "1234567890";
        User user = new User("", testNumber, "");
        assertEquals(user.getPhoneNumber(), testNumber);
        testNumber = "0987654321";
        user.setUsername(testNumber);
        assertEquals(user.getPhoneNumber(), testNumber);
    }

    @Test
    public void testEmailAddress(){
        String testEmail = "test@domain.com";
        User user = new User("", "", testEmail);
        assertEquals(user.getEmailAddress(), testEmail);
        testEmail = "new@domain.com";
        user.setUsername(testEmail);
        assertEquals(user.getEmailAddress(), testEmail);
    }

    @Test
    public void testRequestedTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        User user = new User("","","");
        user.setRequestedTasks(tasks);
        assertEquals(user.getRequestedTasks(), tasks);
    }

    @Test
    public void testBids(){
        ArrayList<Bid> bids = new ArrayList<>();
        User user = new User("","","");
        user.setBids(bids);
        assertEquals(user.getBids(), bids);
    }

    @Test
    public void testAssignedTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        User user = new User("","","");
        user.setAssignedTasks(tasks);
        assertEquals(user.getAssignedTasks(), tasks);
    }
}
*/