package com.lateral.lateral.service;

import com.lateral.lateral.model.Bid;

import java.util.ArrayList;

public interface BidService extends BaseService<Bid> {

    Bid getLowestBid(String taskID);

    ArrayList<Bid> getAllBidsByTaskID(String taskID);
}
