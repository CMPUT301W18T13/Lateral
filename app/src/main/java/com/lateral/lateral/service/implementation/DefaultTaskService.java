/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.lateral.lateral.helper.StringHelper;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.PhotoGallery;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Service for interfacing with Tasks on the ElasticSearch server
 * Note; any failure to return results will return null, so check accordingly
 */
public class DefaultTaskService extends DefaultBaseService<Task> implements TaskService {

    @Override
    protected GsonBuilder buildGson() {
        // Used to serialize PhotoGallery
        return super.buildGson()
                .registerTypeAdapter(PhotoGallery.class, new PhotoGallery.Serializer());
    }

    /**
     * Returns the list of tasks matching the supplied query keywords
     * @param query The keywords to search tasks from
     * @return List of tasks matching query
     */
    // TODO: Purge this method completely, it shouldn't be used
    @Deprecated
    public ArrayList<Task> getAllTasks(String query) throws ServiceException {
        query = StringHelper.makeJsonSafe(query);
        String json = "{\"size\" : " + RECORD_COUNT + ", \"query\": {\"match\": {\"description\": \"" + query + "\"}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + search(json) + "]", listType);
    }

    /**
     * Returns the list of all the tasks requested by a certain user
     * @param requesterID The ID of the requester
     * @return The list associated tasks
     */
    public ArrayList<Task> getAllTasksByRequesterID(String requesterID) throws ServiceException {
        String json = "{\"size\" : " + RECORD_COUNT + ", \"query\":{\"match\":{\"requestingUserId\":{\"query\":\"" + requesterID + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + search(json) + "]", listType);
    }

    /*
    deletes task and all associated bids
     */

    /**
     * Deletes the task and all associated bids
     * @param taskID The ID of the task
     */
    @Override
    public void delete(String taskID) throws ServiceException {
        if (taskID == null){
            throw new IllegalArgumentException("Null passed");
        }
        BidService bidService = new DefaultBidService();
        bidService.deleteBidsByTask(taskID);
        super.delete(taskID);
    }

    /**
     * Gets all the tasks within a certain distance of given coordinates
     * @param latitude Latitude of specified location
     * @param longitude Longitude of specified location
     * @param distance Distance from specified location
     * @return The list of all Tasks within distance of location
     */
    public ArrayList<Task> getAvailableTasksByDistance(Double latitude, Double longitude, Double distance) throws ServiceException {
        String json = "{\"size\" : " + RECORD_COUNT + ", " +
                "\"query\" : { " +
                "\"filtered\" : { " +
                "\"query\" : {" +
                "\"match\" : { \"taskStatus\" : \"Requested Bidded\"}}," +
                "\"filter\" : { " +
                "\"geo_distance\" : { " +
                "\"distance\": \"" + Double.toString(distance) + "km\", " +
                "\"geo_location\" : { " +
                "\"lat\": " + Double.toString(latitude) + "," +
                "\"lon\": " + Double.toString(longitude) + "}}}}}}";
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();

        return gson.fromJson("[" + search(json) + "]", listType);
    }

    public ArrayList<Task> getEveryAvailableTask() throws ServiceException {
        String json = "{\"size\" : " + RECORD_COUNT + ", \"query\" : {\"match\" : {\"taskStatus\" : { \"query\" : \"Requested Bidded\"}}}}";

        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        return gson.fromJson("[" + search(json) + "]", listType);

    }

    // TODO: VITAL ask Tyler to implement this
    /*
    should behave similiar to getEveryAvailableTasks (only display with status' Requested and Bidded) but add query searching
     */
    public ArrayList<Task> getEveryAvailableTaskViaQuery(String query) throws ServiceException {
        query = StringHelper.makeJsonSafe(query);

        String json = "{\"size\" : " + RECORD_COUNT + ",\"query\" : {\"match\" : {\"description\" : { \"query\" : \"Requested Bidded\"}}}}";

        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        return gson.fromJson("[" + search(json) + "]", listType);

    }

    // nick
    public Task getTaskByTaskID(String taskID) throws ServiceException {
        String json = "{\"query\": {\"match\": {\"id\": \"" + taskID + "\"}}}";
        return gson.fromJson(search(json), Task.class);
    }

    // nick
    public ArrayList<Task> getTasksByBidder(String bidderID) throws ServiceException {
        DefaultBidService defaultBidService = new DefaultBidService();
        ArrayList<Bid> userBids = defaultBidService.getAllBidsByUserID(bidderID);       // SearchQuery
        ArrayList<Task> biddedTasks = new ArrayList<Task>();


        for (Bid bid : userBids) {
            Task task = getTaskByTaskID(bid.getTaskId());           // Search query
            biddedTasks.add(task);
        }
        return biddedTasks;
    }

}

