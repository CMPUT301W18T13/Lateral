/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.math.BigDecimal;

/**
 * Represents a bid object that is associated with a certain task
 */
@ElasticSearchType(Name = "Bid")
public class Bid extends BaseEntity {
    // TODO: Need to add more getters/setters
    // Base fields
    private BigDecimal amount;
    private String bidderId;
    private String taskId;
    private BidStatus status;

    // Extra fields
    private transient User bidder;
    private transient Task task;

    // Private constructor for Jest to use
    private Bid(){}

    /**
     * Constructor for the Bid
     * @param amount The requested amount of the Bid
     * @param taskId The ID of the task this Bid is associated with
     * @param bidderId The ID of the User who made this Bid
     */
    public Bid (BigDecimal amount, String taskId, String bidderId){
        if (amount.compareTo(new BigDecimal(Constants.MIN_BID_AMOUNT)) < 0){
            throw new IllegalArgumentException("Amount is below " + Constants.MIN_BID_AMOUNT);
        }

        this.amount = amount;
        this.status = BidStatus.POSTED;
        this.taskId = taskId;
        this.bidderId = bidderId;
    }

    /**
     * Gets the amount of this bid
     * @return The stored amount
     */
    public BigDecimal getAmount(){ return this.amount; }

    /**
     * Gets the bidder on this object
     * @return The associated bidder
     */
    public User getBidder() { return this.bidder; }

    /**
     * Gets the task associated with this Bid
     * @return The corresponding Task
     */
    public Task getTaskBidOn() { return this.task; }

    /**
     * Gets the status of this Bid
     * @return The Bid's status
     */
    public BidStatus getStatus() {return this.status; }

    /**
     * Returns the ID of the task associated with this bid
     * @return The ID of the associate task
     */
    public String getTaskId(){return this.taskId; }

    /**
     * Sets the status of the bid
     * @param status The status to be set
     */
    // If an assigned task is set to requested are all bids removed?
    public void setStatus(BidStatus status) {
        if (this.status != BidStatus.POSTED){
            throw new IllegalArgumentException("This has already been declined or accepted");
        }
        this.status = status;
    }
}
