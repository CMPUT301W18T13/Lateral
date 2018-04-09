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
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a Task in the service
 */
@ElasticSearchType(Name = "Task")
public class Task extends BaseEntity {

    // Base fields
    private String title;
    private Date date;
    private TaskStatus taskStatus;
    private String description;
    private String requestingUserId;
    private String assignedBidId;
    private int bidsNotViewed;
    private int bidsPendingNotification;
    private String requestingUserUsername;
    private BigDecimal lowestBidValue;
    private PhotoGallery photoGallery;
    private String address;

    private double latitude;
    private double longitude;
    @SuppressWarnings({"UnusedDeclaration","FieldCanBeLocal"})
    private String geo_location; // Needed for ElasticSearch query

    // Extra fields
    private transient User requestingUser;
    private transient Bid assignedBid;
    private transient ArrayList<Bid> bids;

    // Private constructor for Jest to use
    private Task(){}

    /**
     * Constructor for the task
     * @param title The title of the task
     */
    public Task (String title) {
        this.setTitle(title);
        date = new Date();
        this.photoGallery = new PhotoGallery();
        this.taskStatus = TaskStatus.Requested;
    }

    /**
     * Constructor for the task
     * @param title The tile of the task
     * @param description The description the task
     */
    public Task (String title, String description) {
        this(title);
        this.setDescription(description);
    }

    /* Setters */

    /**
     * Sets the title of the task
     * @param newTitle The title to be set
     */
    public void setTitle(String newTitle) {

        if (newTitle.length() > Constants.TITLE_CHAR_LIMIT){
            throw new IllegalArgumentException("Username exceeds "
                    + Constants.TITLE_CHAR_LIMIT + " characters");
        }
        this.title = newTitle;
    }

    /**
     * Sets the date of the task
     * @param newDate The date to be set
     */
    public void setDate(Date newDate) {
        this.date = newDate;
    }

    /**
     * Sets the status of the task
     * @param newStatus The status to be set
     */
    public void setStatus(TaskStatus newStatus) {
        this.taskStatus = newStatus;
    }

    /**
     * Sets the description of the task
     * @param newDescription The description to be set
     */
    public void setDescription(String newDescription) {

        if (newDescription.length() > Constants.DESCRIPTION_CHAR_LIMIT){
            throw new IllegalArgumentException("Description exceeds "
                    + Constants.DESCRIPTION_CHAR_LIMIT + " characters");
        }
        this.description = newDescription;
    }

    /**
     * Sets the bids on the task
     * @param newBids The bids to be set
     */
    public void setBids(ArrayList<Bid> newBids) {
        this.bids = newBids;
    }


    /*  getters */

    /**
     * Gets the title of the task
     * @return The title of the task
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the date of the task
     * @return The date of the task
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Gets the status of the task
     * @return The status of the task
     */
    public TaskStatus getStatus() {
        return this.taskStatus;
    }

    /**
     * Gets the description of the task
     * @return The description of the task
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the bids on the task
     * @return The bids on the task
     */
    public ArrayList<Bid> getBids() {
        return this.bids;
    }

    /**
     * Gets the user who requested the task
     * @return The user who requested the task
     */
    public User getRequestingUser() {
        return requestingUser;
    }

    /**
     * Sets the user who requested the task
     * @param requestingUser The user to be set
     */
    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    /**
     * Gets the ID of the user who requested the task
     * @return The ID of th user who requested the task
     */
    public String getRequestingUserId() {
        return requestingUserId;
    }

    public String getRequestingUserUsername() {
        return requestingUserUsername;
    }

    public void setRequestingUserUsername(String requestingUserUsername){
        this.requestingUserUsername = requestingUserUsername;
    }


    public void setLowestBidValue(BigDecimal newLowestBidValue) {
        this.lowestBidValue = newLowestBidValue;
    }

    public BigDecimal getLowestBidValue() {
        if (lowestBidValue == null){
            return null;
        }
        return lowestBidValue.setScale(2, RoundingMode.CEILING);
    }


    /**
     * Sets the ID of the requesting user
     * @param requestingUserId The ID to be set
     */
    public void setRequestingUserId(String requestingUserId) {
        this.requestingUserId = requestingUserId;
    }

    /**
     * Get the bid assigned to the task
     * @return The bid assigned to the task
     */
    public Bid getAssignedBid() {
        return assignedBid;
    }

    /**
     * Sets the bid assigned to the task
     * @param assignedBid The bid to be set
     */
    public void setAssignedBid(Bid assignedBid) {
        this.assignedBid = assignedBid;
    }

    public String getAssignedBidId() {
        return assignedBidId;
    }

    /**
     * Sets the bid assigned to the task
     * @param assignedBidId The user to be set
     */
    public void setAssignedBidId(String assignedBidId) {
        this.assignedBidId = assignedBidId;
    }

    // set geolocation of this task for searching

    /**
     * Sets the latitude and longitude of the task
     * @param latitude Latitude to be set
     * @param longitude Longitude to be set
     */
    public void setLocation(double latitude, double longitude, String address){
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.geo_location = Double.toString(latitude) + ", " +  Double.toString(longitude);
    }

    public double getLat(){
        return latitude;
    }

    public double getLon(){
        return longitude;
    }

    public boolean checkGeo(){
        return address != null;
    }

    public String getAddress(){
        return address;
    }

    /**
     * Adds a given bid to the list of bids
     * @param bid The bid to add
     */
    public void addBid(Bid bid){this.bids.add(bid);}

    public void setBidsNotViewed(int bidsNotViewed){this.bidsNotViewed = bidsNotViewed;}

    public int getBidsNotViewed(){return this.bidsNotViewed;}

    public void setBidsPendingNotification(int bidsPendingNotification){this.bidsPendingNotification = bidsPendingNotification;}

    public int getBidsPendingNotification(){return this.bidsPendingNotification;}

    public PhotoGallery getPhotoGallery() {
        return photoGallery;
    }
    public void setPhotoGallery(PhotoGallery gallery) {
        photoGallery = gallery;
    }
}
