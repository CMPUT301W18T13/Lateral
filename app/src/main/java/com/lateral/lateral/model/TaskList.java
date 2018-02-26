package com.lateral.lateral.model;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks = new ArrayList<Task>();

    public void add(Task task){
        if (this.has(task)){
            throw new IllegalArgumentException();
        }

        tasks.add(task);
    }

    public boolean has(Task task) {
        for (Task taskInstance: tasks){
            if (taskInstance.getId().equals(task.getId())){
                return true;
            }
        }
        return false;
    }

    public Task get(Task task) {
        for (Task taskInstance: tasks){
            if (taskInstance.getId().equals(task.getId())){
                return taskInstance;
            }
        }
        throw new IllegalArgumentException("Task does not exist");
    }

    public void delete(Task task){ tasks.remove(task); }

    public ArrayList<Task> getAll(){ return tasks; }

    public int getCount(){ return tasks.size(); }
}
