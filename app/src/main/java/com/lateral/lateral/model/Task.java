/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a Task in the service
 */
@ElasticSearchType(Name = "Task")
public class Task extends BaseEntity {

    // TODO: Need to add more getters/setters
    // Base fields
    private String title;
    private Date date;
    private int status;                 // may change depending on how we store status
    private String description;
    private String requestingUserId; // tyler AWItlpRj42PX8bQQT0op // nick AWItlpZ842PX8bQQT0oq
    private String assignedUserId;
    private String geo_location; // string so that we can easily serialize a tasks latitude and longitude for geosearch

    // Extra fields
    private transient User requestingUser;
    private transient User assignedUser;
    private transient ArrayList<Bid> bids;
    private transient Bid lowestBid;

    // TODO: Implement location variable
    // TODO: Implement photo storage allocation
    private transient double latitude;
    private transient double longitude;
    //
    // Private constructor for Jest to use
    private Task(){}

    /**
     * Constructor for the task
     * @param title The title of the task
     */
    public Task (String title) {
        this.setTitle(title);
        date = new Date();
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
    public void setStatus(int newStatus) {
        this.status = newStatus;
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
    public int getStatus() {
        return this.status;
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

    /**
     * Sets the ID of the requesting user
     * @param requestingUserId The ID to be set
     */
    public void setRequestingUserId(String requestingUserId) {
        this.requestingUserId = requestingUserId;
    }

    /**
     * Gets the lowest bid
     * @return The lowest bid
     */
    public Bid getLowestBid() {
        return lowestBid;
    }

    /**
     * Sets the lowest bid
     * @param lowestBid The bid to be set
     */
    public void setLowestBid(Bid lowestBid) {
        this.lowestBid = lowestBid;
    }

    /**
     * Get the user assigned to the task
     * @return The user assigned to the task
     */
    public User getAssignedUser() {
        return assignedUser;
    }

    /**
     * Sets ths user assigned to the task
     * @param assignedUser The user to be set
     */
    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    /**
     * Gets the ID of the assigned user
     * @return The ID of the assigned user
     */
    public String getAssignedUserId() {
        return assignedUserId;
    }

    /**
     * Sets the ID of the assigned user
     * @param assignedUserId The ID to be set
     */
    public void setAssignedUserId(String assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    // set geolocation of this task for searching

    /**
     * Sets the latitude and longitude of the task
     * @param latitude Latitude to be set
     * @param longitude Longitude to be set
     */
    public void setLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.geo_location = Double.toString(latitude) + ", " +  Double.toString(longitude);
    }
}
