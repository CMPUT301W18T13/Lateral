/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.TaskService;

import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Service for interfacing with Tasks on the ElasticSearch server
 * Note; any failure to return results will return null, so check accordingly
 */
public class DefaultTaskService extends DefaultBaseService<Task> implements TaskService {
    /**
     * Returns the Task based on the title
     * @param title Title to use in query
     * @return The associated task if it exists; null otherwise
     */
    public Task getTaskByTitle(String title){
        String json = "{\"query\": {\"match\": {\"title\": \"" + title + "\"}}}";
        return gson.fromJson(get(json), Task.class);
    }


    /**
     * Returns the list of tasks matching the supplied query keywords
     * @param query The keywords to get tasks from
     * @return List of tasks matching query
     */
    public ArrayList<Task> getAllTasks(String query){
        String json = "{\"query\": {\"match\": {\"title\": \"" + query + "\"}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + get(json) + "]", listType);
    }

    /**
     * Returns the list of all the tasks requested by a certain user
     * @param requesterID The ID of the requester
     * @return The list associated tasks
     */
    public ArrayList<Task> getAllTasksByRequesterID(String requesterID){
        String json = "{\"query\":{\"match\":{\"requestingUserId\":{\"query\":\"" + requesterID + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + get(json) + "]", listType);
    }

    /*
    deletes task and all associated bids
     */

    /**
     * Deletes the task and all associated bids
     * @param taskID The ID of the task
     */
    @Override
    public void delete(String taskID){
        DefaultBidService defaultBidService = new DefaultBidService();
        ArrayList<Bid> taskBids = defaultBidService.getAllBidsByTaskID(taskID);

        if(taskBids != null) {
            for (Bid bid : taskBids) {
                defaultBidService.delete(bid.getId());
            }
        }

        super.delete(taskID);
    }

    /**
     * Gets all the tasks within a certain distance of given coordinates
     * @param latitude Latitude of specified location
     * @param longitude Longitude of specified location
     * @param distance Distance from specified location
     * @return The list of all Tasks within distance of location
     */
    public ArrayList<Task> getAllTasksByDistance(Double latitude, Double longitude, Double distance){
        String json = "{" +
                "\"query\" : { " +
                "\"filtered\" : { " +
                "\"filter\" : { " +
                "\"geo_distance\" : { " +
                "\"distance\": \"" + Double.toString(distance) + "km\", " +
                "\"geo_location\" : { " +
                "\"lat\": " + Double.toString(latitude) + "," +
                "\"lon\": " + Double.toString(longitude) + "}}}}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + get(json) + "]", listType);
    }

    public ArrayList<Task> getEveryTask(){
        String json = "{ \"query\" : { \"match_all\" : {}}}";

        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        return gson.fromJson("[" + get(json) + "]", listType);

    }

    // nick
    public Task getTaskByTaskID(String taskID){
        String json = "{\"query\": {\"match\": {\"id\": \"" + taskID + "\"}}}";
        return gson.fromJson(get(json), Task.class);
    }

    // nick
    public ArrayList<Task> getBiddedTasks(String bidderID) {
        DefaultBidService defaultBidService = new DefaultBidService();
        ArrayList<Bid> userBids = defaultBidService.getAllBidsByUserID(bidderID);       // SearchQuery
        ArrayList<Task> biddedTasks = new ArrayList<Task>();


        for (Bid bid : userBids) {
            /*
            should be substituted with bid.getTaskBidOn() but doesnt appear to be implemented
             */
            Task task = getTaskByTaskID(bid.getTaskId());           // Search query
            biddedTasks.add(task);
        }
        return biddedTasks;
    }

}

