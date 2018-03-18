package com.lateral.lateral.service.implementation;

import com.google.common.reflect.TypeToken;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.BidService;

import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class DefaultBidService extends DefaultBaseService<Bid> implements BidService{

    public Bid getLowestBid(String taskID){
        String json = "{\"query\": " +
                "{\"match\": " +
                "{\"taskId\": \"" + taskID + "\"}}, " +
                "\"sort\" : [{\"amount\" : { \"order\" : \"asc\"}}], " +
                "\"size\" : 1}";

        return gson.fromJson(get(json), Bid.class);
    }

    public ArrayList<Bid> getAllBidsByTaskID(String taskID) {
        String json = "{\"query\": {\"match\": {\"taskId\": {\"query\" : \"" + taskID + "\"}}}}";
        Type listType = new TypeToken<ArrayList<Bid>>(){}.getType();
        return gson.fromJson("[" + get(json) + "]", listType);
    }
}
