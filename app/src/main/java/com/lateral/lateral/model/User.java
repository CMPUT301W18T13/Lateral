package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.util.ArrayList;

@ElasticSearchType(Name = "User")
public class User extends BaseEntity {

    // Base fields
    private String username;
    private String phoneNumber;
    private String emailAddress;
    private String saltAndHash;
    private String token;

    // Extra fields
    // transient to mark as non-serializable
    private transient ArrayList<Task> requestedTasks;
    private transient ArrayList<Bid> bids;
    private transient ArrayList<Task> assignedTasks;

    // Private constructor for Jest to use
    private User(){}

    public User(String username, String phoneNumber, String emailAddress, String saltAndHash){
        this.setUsername(username);
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.saltAndHash = saltAndHash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username.length() > Constants.USERNAME_CHAR_LIMIT){
            throw new IllegalArgumentException("Username exceeds "
                    + Constants.USERNAME_CHAR_LIMIT + " characters");
        }
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSaltAndHash() { return this.saltAndHash; }

    public void setSaltAndHash(String saltAndHash) { this.saltAndHash = saltAndHash; }

    public String getToken() { return this.token; }

    public void setToken(String token) { this.token = token; }

    public ArrayList<Task> getRequestedTasks() {
        return requestedTasks;
    }

    public void setRequestedTasks(ArrayList<Task> requestedTasks) {
        this.requestedTasks = requestedTasks;
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public void setBids(ArrayList<Bid> bids) {
        this.bids = bids;
    }

    public ArrayList<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(ArrayList<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }
}

