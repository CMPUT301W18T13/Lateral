package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ServiceIndex;

import java.math.BigDecimal;

@ServiceIndex(Name = "Bid")
public class Bid extends BaseEntity {
    private BigDecimal amount;
    private User bidder;
    private Task taskBidOn;
    private BidStatus status;

    // Private constructor for Jest to use
    private Bid(){}

    public Bid (BigDecimal amount, User bidder, Task taskBidOn){
        if (amount.compareTo(new BigDecimal(Constants.MIN_BID_AMOUNT)) < 0){
            throw new IllegalArgumentException("Amount is below " + Constants.MIN_BID_AMOUNT);
        }

        if (bidder == null){
            throw new IllegalArgumentException("Must have a bidder");
        }

        if (taskBidOn == null){
            throw new IllegalArgumentException("This bid must belong to a task");
        }

        this.amount = amount;
        this.bidder = bidder;
        this.taskBidOn = taskBidOn;
        this.status = BidStatus.POSTED;
    }

    public BigDecimal getAmount(){ return this.amount; }

    public User getBidder() { return this.bidder; }

    public Task getTaskBidOn() { return this.taskBidOn; }

    public BidStatus getStatus() {return this.status; }

    // If an assigned task is set to requested are all bids removed?
    public void setStatus(BidStatus status) {
        if (this.status != BidStatus.POSTED){
            throw new IllegalArgumentException("This has already been declined or accepted");
        }
        this.status = status;
    }


}
