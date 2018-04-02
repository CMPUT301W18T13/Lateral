/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a list of tasks
 */
public class TaskList{
    private ArrayList<Task> tasks = new ArrayList<Task>();

    /**
     * Adds a task to the list
     * @param tasks Task to be added
     */

    public void setTasks(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    public void add(Task task){
        tasks.add(task);
    }

    /**
     * Returns whether or not a given task is in the list
     * @param task Task to check
     * @return True if the task is in the list; false otherwise
     */
    public boolean has(Task task) {
        for (Task taskInstance: tasks){
            if (taskInstance.getId().equals(task.getId())){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a task from the list
     * @param task The task to get
     * @return The task from the list
     */
    public Task get(Task task) {
        for (Task taskInstance: tasks){
            if (taskInstance.getId().equals(task.getId())){
                return taskInstance;
            }
        }
        throw new IllegalArgumentException("Task does not exist");
    }

    /**
     * Deletes a task from the list
     * @param task Task to be deleted
     */
    public void delete(Task task){ tasks.remove(task); }

    /**
     * Gets the list of all the tasks
     * @return the list of all the tasks
     */
    public ArrayList<Task> getAll(){ return tasks; }

    /**
     * Gets the count of all tasks in the list
     * @return the number of tasks
     */
    public int getCount(){ return tasks.size(); }
}
