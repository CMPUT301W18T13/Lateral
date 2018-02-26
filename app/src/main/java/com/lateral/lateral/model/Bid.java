package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ServiceIndex;

@ServiceIndex(Name = "Bid")
public class Bid extends BaseEntity {
    private float amount;
    private String bidder;
    private String taskBidOn;
    private int status;

    public Bid (float amount, String bidder, String taskBidOn){
        if (amount < Constants.MIN_BID_AMOUNT){
            throw new IllegalArgumentException("Amount is below " + Constants.MIN_BID_AMOUNT);
        }

        if (bidder.equals("")){
            throw new IllegalArgumentException("Must have a bidder");
        }

        if (taskBidOn.equals("")){
            throw new IllegalArgumentException("This bid must belong to a task");
        }

        this.amount = amount;
        this.bidder = bidder;
        this.taskBidOn = taskBidOn;
        this.status = Constants.BID_POSTED;
    }

    public float getAmount(){ return this.amount; }

    public String getBidder() { return this.bidder; }

    public String getTaskBidOn() { return  this.taskBidOn; }

    public int getStatus() {return this.status; }

    // If an assigned task is set to requested are all bids removed?
    public void setStatus(int status) {
        if (this.status != Constants.BID_POSTED){
            throw new IllegalArgumentException("This has already been declined or accepted");
        }
        this.status = status;
    }


}
