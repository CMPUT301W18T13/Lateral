package com.lateral.lateral.service.implementation;

import com.lateral.lateral.model.Bid;
import com.lateral.lateral.service.BidService;

import org.apache.commons.lang3.NotImplementedException;


public class DefaultBidService extends DefaultBaseService<Bid> implements BidService{

    public Bid getLowestBid(String taskID){
        throw new NotImplementedException("Implement me!");
        // TODO: Implement this
    }
}
