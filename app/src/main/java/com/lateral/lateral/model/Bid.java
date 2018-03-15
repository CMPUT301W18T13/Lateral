package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.math.BigDecimal;

@ElasticSearchType(Name = "Bid")
public class Bid extends BaseEntity {
    // TODO: Need to add more getters/setters
    // Base fields
    private BigDecimal amount;
    private String bidderId = "npwhite";
    private String taskId;
    private BidStatus status;

    // Extra fields
    private transient User bidder;
    private transient Task task;

    // Private constructor for Jest to use
    private Bid(){}

    public Bid (BigDecimal amount, String taskId){
        if (amount.compareTo(new BigDecimal(Constants.MIN_BID_AMOUNT)) < 0){
            throw new IllegalArgumentException("Amount is below " + Constants.MIN_BID_AMOUNT);
        }

        this.amount = amount;
        this.status = BidStatus.POSTED;
        this.taskId = taskId;
    }

    public BigDecimal getAmount(){ return this.amount; }

    public User getBidder() { return this.bidder; }

    public Task getTaskBidOn() { return this.task; }

    public BidStatus getStatus() {return this.status; }

    public String getTaskId(){return this.taskId; }

    // If an assigned task is set to requested are all bids removed?
    public void setStatus(BidStatus status) {
        if (this.status != BidStatus.POSTED){
            throw new IllegalArgumentException("This has already been declined or accepted");
        }
        this.status = status;
    }


}
