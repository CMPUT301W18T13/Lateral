/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.ServiceException;

import java.util.ArrayList;

/**
 * Interface for dealing with Bids in the database
 */
public interface BidService extends BaseService<Bid> {
    /**
     * Gets the lowest bid from a certain task, based on Jest ID
     * @param taskID Jest ID of the task
     * @return The lowest bid
     */
    Bid getLowestBid(String taskID) throws ServiceException;

    /**
     * Delete all bids for the given task
     * @param taskID Jest ID of the task
     */
    void deleteBidsByTask(String taskID) throws ServiceException;

    ArrayList<Bid> getAllBidsByTaskIDAmountSorted(String taskID) throws ServiceException;

    ArrayList<Bid> getAllBidsByTaskIDDateSorted(String taskID) throws ServiceException;

    ArrayList<Bid> getAllBidsByUserID(String userId) throws ServiceException;

    void deleteOtherBidsByTask(String taskID, String keepBidId) throws ServiceException;

}
