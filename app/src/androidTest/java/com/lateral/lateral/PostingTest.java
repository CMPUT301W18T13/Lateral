package com.lateral.lateral;

import android.support.test.runner.AndroidJUnit4;

import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class PostingTest {


    @Test
    public void testPostTask() {

//        DefaultTaskService defaultTaskService = new DefaultTaskService();
//        Task testTask;

//        testTask = new Task("Garbage Pickup", "Picking up roadside garbage");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Snow Removal", "Would like someone to help shovel my driveway");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Babysitter Wanted", "URGENT: need a babysitter for tonight");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Math Tutor needed", "I need someone to teach me math");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Dog walker", "I need a dog walker");
//        defaultTaskService.post(testTask);

//        testTask = new Task("deliver food", "bring food to a place");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Volunteer job", "Work for a day as a volunteer");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Photographer needed", "Photographer needed for 2-day shoot");
//        defaultTaskService.post(testTask);
//        testTask = new Task("Paint a picture", "Need an artist to make a painting for me");
//        defaultTaskService.post(testTask);

//        DefaultTaskService defaultTaskService = new DefaultTaskService();
//        Task testTask = new Task("safe walker needed", "walker to walk people home");
//        defaultTaskService.post(testTask);
    }

    @Test
    public void testPostBid() {
        //Bid bid = new Bid(new BigDecimal("25"), "AWIn9KMR-p3BIk3Z2HhS");
        //DefaultBidService defaultBidService = new DefaultBidService();
        //defaultBidService.post(bid);
//        Bid bid = new Bid(new BigDecimal("20"), "AWIoBBGl-p3BIk3Z2Hho");
//        DefaultBidService defaultBidService = new DefaultBidService();
//        defaultBidService.post(bid);
    }

    @Test
    public void testCreateUser() {
//        User testUser;
//        DefaultUserService defaultUserService = new DefaultUserService();
//        testUser = new User("tlaz", "555-555-5555", "email@gmail.ca", "idkl");
//        defaultUserService.post(testUser);
//        testUser = new User("npwhite", "555-555-5555", "coolguy@gmail.ca", "bhbt");
//        defaultUserService.post(testUser);
    }

    @Test
    public void testGetBidsByTaskID() {
//        ArrayList<Bid> returnedBids;
//        DefaultBidService defaultBidService = new DefaultBidService();
//        returnedBids = defaultBidService.getAllBidsByTaskID("AWIn9KMR-p3BIk3Z2HhS");
//        assertEquals("tlaz", returnedBids.search(0).getBidder());
    }

    @Test
    public void testGetSaltAndHash() {
//        String mUsername = "tlaz";
//        DefaultUserService defaultUserService = new DefaultUserService();
//        String result = defaultUserService.getSaltAndHash(mUsername);
//        assertEquals("idkl", result);
    }

    @Test
    public void testGetEveryTask() {
//        DefaultTaskService defaultTaskService = new DefaultTaskService();
//        ArrayList<Task> returned = defaultTaskService.getEveryAvailableTask();
//        Task task1 = returned.search(0);
//        assertEquals(task1.getTitle(), "Test");
    }

    @Test
    public void testGetLowestBid() {
//        DefaultBidService defaultBidService = new DefaultBidService();
//        Bid lowestBid = defaultBidService.getLowestBid("AWJACBVhAJsZenWtuLON");
    }

    // currently only deletes 50
    /*@Test
    public void testDeleteAllTasks() {
        DefaultTaskService defaultTaskService = new DefaultTaskService();
        //defaultTaskService.delete("AWJACBVhAJsZenWtuLON");
        ArrayList<Task> allTasks = defaultTaskService.getEveryAvailableTask();
        String taskId;
        for (Task task : allTasks) {
            taskId = task.getId();
            defaultTaskService.delete(taskId);
        }
    }*/


}
