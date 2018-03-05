package com.lateral.lateral.service.implementation;

import com.lateral.lateral.model.Bid;
import com.lateral.lateral.service.BidService;


public class DefaultBidService extends DefaultBaseService<Bid> {
    // TODO: Add extra methods specific to the Bid index

    public Bid getBidById(String id){
        String json = "{\"query\": {\"match\": {\"_id\": \"" + id + "\"}}}";
        return gson.fromJson(get(json), Bid.class);
    }

}
