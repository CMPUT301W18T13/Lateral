package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ServiceIndex;

import java.util.ArrayList;
import java.util.Date;

@ServiceIndex(Name = "Task")
public class Task extends BaseEntity {

    private String title;
    private Date date;
    private int status;                 // may change depending on how we store status
    private String description;
    private User requestingUser;
    private User providingUser;         // initially set to null
    private ArrayList<Bid> bids;

    // TODO: Implement location variable
    // TODO: Implement photo storage allocation

    // Private constructor for Jest to use
    private Task(){}

    public Task (String title) {
        this.setTitle(title);
        date = new Date();
    }

    public Task (String title, User requestingUser) {
        this(title);
        this.requestingUser = requestingUser;
    }

    public Task (String title, User requestingUser, String description) {
        this(title, requestingUser);
        this.setDescription(description);
    }


    /* Setters */
    public void setTitle(String newTitle) {

        if (newTitle.length() > Constants.USERNAME_CHAR_LIMIT){
            throw new IllegalArgumentException("Username exceeds "
                    + Constants.USERNAME_CHAR_LIMIT + " characters");
        }
        this.title = newTitle;
    }

    public void setDate(Date newDate) {
        this.date = newDate;
    }

    public void setStatus(int newStatus) {
        this.status = newStatus;
    }

    public void setDescription(String newDescription) {

        if (newDescription.length() > Constants.DESCRIPTION_CHAR_LIMIT){
            throw new IllegalArgumentException("Description exceeds "
                    + Constants.DESCRIPTION_CHAR_LIMIT + " characters");
        }
        this.description = newDescription;
    }

    public void setProvidingUser(User newProvidingUser) {
        this.providingUser = newProvidingUser;
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

    public int getStatus() {
        return this.status;
    }

    public String getDescription() {
        return this.description;
    }

    public User getProvidingUser() {
        return this.providingUser;
    }

    public ArrayList<Bid> getBids() {
        return this.bids;
    }
}
