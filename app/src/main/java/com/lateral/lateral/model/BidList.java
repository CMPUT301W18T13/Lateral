/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import java.util.ArrayList;

/**
 * Represents a list of bids
 */
public class BidList {
    private ArrayList<Bid> bids = new ArrayList<Bid>();

    /**
     * Adds a Bid to the list
     * @param bid The Bid to add
     */
    public void add(Bid bid){
        bids.add(bid);
    }

    /**
     * Verifies whether a certain bid is in the list
     * @param bid The bid to check
     * @return True if the bid is in the list; false otherwise
     */
    public boolean has(Bid bid) {
        for (Bid bidInstance: bids){
            if (bidInstance.getId().equals(bid.getId())){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the bid in the list based on ID
     * @param bid The bid to get
     * @return The bid in the list
     */
    public Bid get(Bid bid) {
        for (Bid bidInstance: bids){
            if (bidInstance.getId().equals(bid.getId())){
                return bidInstance;
            }
        }
        throw new IllegalArgumentException("Bid does not exist");
    }

    /**
     * Deletes a bid from the list
     * @param bid The bid to delete
     */
    public void delete(Bid bid){ bids.remove(bid); }

    /**
     * Gets all the bids in the list
     * @return The list of all the bids
     */
    public ArrayList<Bid> getAll(){ return bids; }

    /**
     * Gets the number of bids in the list
     * @return The number of bids in the list
     */
    public int getCount(){ return bids.size(); }

}
