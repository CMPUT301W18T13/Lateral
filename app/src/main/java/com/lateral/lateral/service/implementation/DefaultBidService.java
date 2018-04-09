/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import com.google.common.reflect.TypeToken;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.service.BidService;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Service class to interface with Bids on the ElasticSearch server
 */
public class DefaultBidService extends DefaultBaseService<Bid> implements BidService{

    /**
     * Gets the lowest bid on a certain task
     * @param taskID Jest ID of the task
     * @return The Bid with the lowest price
     */
    @Override
    public Bid getLowestBid(String taskID) throws ServiceException {
        String json = "{\"query\": " +
                "{\"match\": " +
                "{\"taskId\": \"" + taskID + "\"}}, " +
                "\"sort\" : [{\"amount\" : { \"order\" : \"asc\"}}], " +
                "\"size\" : 1}";

        return gson.fromJson(search(json), Bid.class);
    }


    /**
     * Gets all the bids with the specified taskID
     * @param taskID Task ID to search Bids from
     * @return The list of Bids
     */
    public ArrayList<Bid> getAllBidsByTaskIDAmountSorted(String taskID) throws ServiceException {
        String json = "{\"size\" : " + RECORD_COUNT + ", \"query\": {\"match\": {\"taskId\": {\"query\" : \"" + taskID + "\"}}}, " +
                "\"sort\" : [{\"amount\" : { \"order\" : \"asc\"}}]}" ;

        Type listType = new TypeToken<ArrayList<Bid>>(){}.getType();
        return gson.fromJson("[" + search(json) + "]", listType);
    }

    /**
     * Gets all the bids with the specified taskID
     * @param taskID Task ID to search Bids from
     * @return The list of Bids
     */
    public ArrayList<Bid> getAllBidsByTaskIDDateSorted(String taskID) throws ServiceException {
        String json = "{\"size\" : " + RECORD_COUNT + ",\"query\": {\"match\": {\"taskId\": {\"query\" : \"" + taskID + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Bid>>(){}.getType();
        return gson.fromJson("[" + search(json) + "]", listType);
    }

    public ArrayList<Bid> getAllBidsByUserID(String userId) throws ServiceException {
        String json = "{\"size\" : " + RECORD_COUNT + ", \"query\":{\"match\":{\"bidderId\":{\"query\":\"" + userId + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Bid>>() {
        }.getType();
        return gson.fromJson("[" + search(json) + "]", listType);

    }

    /**
     * Delete all bids for the given task
     * @param taskID Jest ID of the task
     */
    public void deleteBidsByTask(String taskID) throws ServiceException {
        if (taskID == null){
            throw new IllegalArgumentException("Null passed");
        }
        ArrayList<Bid> taskBids = getAllBidsByTaskIDAmountSorted(taskID);

        for (Bid bid : taskBids) {
            delete(bid.getId());
        }
    }

    /**
     * Delete all bids for the given task
     * @param taskID Jest ID of the task
     * @param keepBidId the bid to keep
     */
    public void deleteOtherBidsByTask(String taskID, String keepBidId) throws ServiceException {
        if (taskID == null){
            throw new IllegalArgumentException("Null passed");
        }
        ArrayList<Bid> taskBids = getAllBidsByTaskIDAmountSorted(taskID);

        if(taskBids != null) {
            for (Bid bid : taskBids) {
                if (!(bid.getId().equals(keepBidId)))
                    delete(bid.getId());
            }
        }
    }
}
