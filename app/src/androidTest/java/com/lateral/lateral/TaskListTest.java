package com.lateral.lateral;

import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskList;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskListTest {

    @Test
    public void testAdd() {

        TaskList tasks = new TaskList();
        Task task = new Task("Drive me");
        task.setId("MDM");

        assertFalse(tasks.has(task));
        tasks.add(task);
        assertTrue(tasks.has(task));
        try {
            tasks.add(task);
            assertTrue(Boolean.FALSE);
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testHas() {
        TaskList tasks = new TaskList();
        Task task = new Task("Drive me");
        task.setId("MDM");

        assertFalse(tasks.has(task));
        tasks.add(task);
        assertTrue(tasks.has(task));
    }

    @Test
    public void testGetCount(){
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.getCount());
        tasks.add(new Task("Drive me"));
        tasks.add(new Task("Fly me"));
        tasks.add(new Task("Ship me"));
        assertEquals(3, tasks.getCount());
    }

    @Test
    public void testGet() {
        // using index
        TaskList tasks = new TaskList();
        Task task = new Task("Drive me");
        task.setId("MDM");

        tasks.add(task);
        Task returnedTask = tasks.get(task);
        assertEquals(returnedTask.getId(), task.getId());
    }

    @Test
    public void testGetAll() {
        ArrayList<Task> taskArray;
        TaskList tasks = new TaskList();
        tasks.add(new Task("Drive me"));
        tasks.add(new Task("Fly me"));
        tasks.add(new Task("Ship me"));
        taskArray = tasks.getAll();
        assertEquals(3, taskArray.size());
    }

    @Test
    public void testDelete(){

        TaskList tasks = new TaskList();
        Task task = new Task("Drive me");
        task.setId("MDM");

        tasks.add(task);
        tasks.delete(task);
        assertFalse(tasks.has(task));

    }
}
