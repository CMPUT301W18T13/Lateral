/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a bid object that is associated with a certain task
 */
@ElasticSearchType(Name = "Bid")
public class Bid extends BaseEntity {

    // Base fields
    private BigDecimal amount;
    private String bidderId;
    private String taskId;

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

        this.amount = amount.setScale(2, RoundingMode.CEILING); // Needed to keep precision
        this.taskId = taskId;
        this.bidderId = bidderId;
    }

    /**
     * Constructor
     * @param amount The dollar value of the new bid
     */
    public Bid(BigDecimal amount){
        if (amount.compareTo(new BigDecimal(Constants.MIN_BID_AMOUNT)) < 0){
            throw new IllegalArgumentException("Amount is below " + Constants.MIN_BID_AMOUNT);
        }

        this.amount = amount;
    }

    /**
     * Gets the amount of this bid
     * @return The stored amount
     */
    public BigDecimal getAmount(){ return this.amount.setScale(2, RoundingMode.CEILING); }

    /**
     * Gets the bidder on this object
     * @return The associated bidder
     */
    public User getBidder() { return this.bidder; }

    /**
     * Sets the bidder on this object
     * @param bidder the associated bidder to set
     */
    public void setBidder(User bidder) {this.bidder = bidder;}

    /**
     * Gets the task associated with this Bid
     * @return The corresponding Task
     */
    public Task getTaskBidOn() { return this.task; }

    /**
     * Returns the ID of the task associated with this bid
     * @return The ID of the associate task
     */
    public String getTaskId(){return this.taskId; }

    /**
     * Sets the Task ID of the bid
     * @param taskId ID to be set
     */
    public void setTaskId(String taskId){ this.taskId = taskId; }

    /**
     * Sets the Bidder ID of the bid
     * @param bidderId ID to be set
     */
    public void setBidderId(String bidderId){ this.bidderId = bidderId; }

    /**
     * Gets the Bidder ID of the bid
     * @return The Bidder ID
     */
    public String getBidderId(){ return this.bidderId; }
}
