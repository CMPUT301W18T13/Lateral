/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.lateral.lateral.annotation.ElasticSearchType;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.BidService;

import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.searchbox.client.JestResult;
import io.searchbox.core.DeleteByQuery;

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
    public Bid getLowestBid(String taskID){
        String json = "{\"query\": " +
                "{\"match\": " +
                "{\"taskId\": \"" + taskID + "\"}}, " +
                "\"sort\" : [{\"amount\" : { \"order\" : \"asc\"}}], " +
                "\"size\" : 1}";

        return gson.fromJson(get(json), Bid.class);
    }


    /**
     * Gets all the bids with the specified taskID
     * @param taskID Task ID to get Bids from
     * @param offset the offest
     * @return The list of Bids
     */
    public ArrayList<Bid> getAllBidsByTaskIDAmountSorted(String taskID, int offset) {
        Integer next = offset * 10;
        String json = "{\"from\" : " + next.toString() + ", \"size\" : 10, \"query\": {\"match\": {\"taskId\": {\"query\" : \"" + taskID + "\"}}}, " +
                "\"sort\" : [{\"amount\" : { \"order\" : \"asc\"}}]}" ;
        Type listType = new TypeToken<ArrayList<Bid>>(){}.getType();
        return gson.fromJson("[" + get(json) + "]", listType);
    }

    /**
     * Gets all the bids with the specified taskID
     * @param taskID Task ID to get Bids from
     * @return The list of Bids
     */
    public ArrayList<Bid> getAllBidsByTaskIDDateSorted(String taskID, int offset) {
        Integer next = offset * 10;
        String json = "{\"from\" : " + next.toString() + ", \"size\" : 10,\"query\": {\"match\": {\"taskId\": {\"query\" : \"" + taskID + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Bid>>(){}.getType();
        return gson.fromJson("[" + get(json) + "]", listType);
    }

    public ArrayList<Bid> getAllBidsByUserID(String userId) {
        String json = "{\"query\":{\"match\":{\"bidderId\":{\"query\":\"" + userId + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Bid>>() {
        }.getType();
        return gson.fromJson("[" + get(json) + "]", listType);

    }

    /**
     * Delete all bids for the given task
     * @param taskID Jest ID of the task
     */
    public void deleteBidsByTask(String taskID){
        // TODO: Not actually getting all!!!!! need to remove offset
        ArrayList<Bid> taskBids = getAllBidsByTaskIDAmountSorted(taskID, 0);

        for (Bid bid : taskBids) {
            delete(bid.getId());
        }
    }

    /**
     * Delete all bids for the given task
     * @param taskID Jest ID of the task
     * @param keepBidId the bid to keep
     */
    public void deleteOtherBidsByTask(String taskID, String keepBidId){
        // TODO: Not actually getting all!!!!! need to remove offset
        ArrayList<Bid> taskBids = getAllBidsByTaskIDAmountSorted(taskID, 0);

        if(taskBids != null) {
            for (Bid bid : taskBids) {
                if (!(bid.getId().equals(keepBidId)))
                    delete(bid.getId());
            }
        }
    }
}
