package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.util.ArrayList;
import java.util.Date;

@ElasticSearchType(Name = "Task")
public class Task extends BaseEntity {

    // Base fields
    private String title;
    private Date date;
    private TaskStatus status;
    private String description;
    private String requestingUserId;
    private String assignedBidId;
    private String geo_location; // string so that we can easily serialize a tasks latitude and longitude for geosearch

    // Extra fields
    private transient User requestingUser;
    private transient Bid assignedBid;
    private transient ArrayList<Bid> bids;
    private transient Bid lowestBid;

    // TODO: Implement location variable
    // TODO: Implement photo storage allocation
    private transient double latitude;
    private transient double longitude;
    //
    // Private constructor for Jest to use
    private Task(){}

    public Task (String title) {
        this.setTitle(title);
        date = new Date();
    }

    public Task (String title, String description) {
        this(title);
        this.setDescription(description);
    }

    /* Setters */
    public void setTitle(String newTitle) {

        if (newTitle.length() > Constants.TITLE_CHAR_LIMIT){
            throw new IllegalArgumentException("Username exceeds "
                    + Constants.TITLE_CHAR_LIMIT + " characters");
        }
        this.title = newTitle;
    }

    public void setDate(Date newDate) {
        this.date = newDate;
    }

    public void setStatus(TaskStatus newStatus) {
        this.status = newStatus;
    }

    public void setDescription(String newDescription) {

        if (newDescription.length() > Constants.DESCRIPTION_CHAR_LIMIT){
            throw new IllegalArgumentException("Description exceeds "
                    + Constants.DESCRIPTION_CHAR_LIMIT + " characters");
        }
        this.description = newDescription;
    }

    public void setBids(ArrayList<Bid> newBids) {
        this.bids = newBids;
    }


    /*  getters */
    public String getTitle() {
        return this.title;
    }

    public Date getDate() {
        return this.date;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<Bid> getBids() {
        return this.bids;
    }

    public void addBid(Bid bid){
        if (bids == null) {
            bids = new ArrayList<>();
        }
        bids.add(bid);
    }

    public User getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    public String getRequestingUserId() {
        return requestingUserId;
    }

    public void setRequestingUserId(String requestingUserId) {
        this.requestingUserId = requestingUserId;
    }

    public Bid getLowestBid() {
        return lowestBid;
    }

    public void setLowestBid(Bid lowestBid) {
        this.lowestBid = lowestBid;
    }

    public Bid getAssignedBid() {
        return assignedBid;
    }

    public void setAssignedBid(Bid bid) {
        this.assignedBid = assignedBid;
    }

    public String getAssignedBidId() {
        return assignedBidId;
    }

    public void setAssignedBidId(String bidId) {
        this.assignedBidId = bidId;
    }

    // set geolocation of this task for searching
    public void setLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.geo_location = Double.toString(latitude) + ", " +  Double.toString(longitude);
    }
}
