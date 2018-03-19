/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import com.google.common.reflect.TypeToken;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.BidService;

import org.apache.commons.lang3.NotImplementedException;

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
     * @return The list of Bids
     */
    public ArrayList<Bid> getAllBidsByTaskID(String taskID) {
        String json = "{\"query\": {\"match\": {\"taskId\": {\"query\" : \"" + taskID + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Bid>>(){}.getType();
        return gson.fromJson("[" + get(json) + "]", listType);
    }
}
