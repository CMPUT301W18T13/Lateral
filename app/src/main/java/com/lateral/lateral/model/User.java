/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import com.lateral.lateral.Constants;
import com.lateral.lateral.annotation.ElasticSearchType;

import java.util.ArrayList;

/**
 * Represents a User within the service
 */
@ElasticSearchType(Name = "User")
public class User extends BaseEntity {

    // Base fields
    private String username;
    private String phoneNumber;
    private String emailAddress;
    private String saltAndHash;
    private String token;

    // Private constructor for Jest to use
    private User(){}

    /**
     * Constructor for the user
     * @param username The username of the user
     * @param phoneNumber The phone number of the user
     * @param emailAddress The email address of the user
     * @param saltAndHash The salt and hashed password of the user
     */
    public User(String username, String phoneNumber, String emailAddress, String saltAndHash){
        this.setUsername(username);
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.saltAndHash = saltAndHash;
    }

    /**
     * Gets the user's username
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username
     * @param username The username to be set
     */
    public void setUsername(String username) {
        if (username.length() > Constants.USERNAME_CHAR_LIMIT){
            throw new IllegalArgumentException("Username exceeds "
                    + Constants.USERNAME_CHAR_LIMIT + " characters");
        }
        this.username = username;
    }

    /**
     * Gets the user's phone number
     * @return The user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber The phone number to be set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's email address
     * @return The user's email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the user's email address
     * @param emailAddress The email address to be set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the user's salt-and-hash
     * @return The user's email address
     */
    public String getSaltAndHash() { return this.saltAndHash; }

    /**
     * Sets the user's salt-and-hash
     * @param saltAndHash The salt-and-hash to be set
     */
    public void setSaltAndHash(String saltAndHash) { this.saltAndHash = saltAndHash; }

    /**
     * Gets the user's logon token
     * @return The user's email address
     */
    public String getToken() { return this.token; }

    /**
     * Sets the user's logon token
     * @param token The logon token to be set
     */
    public void setToken(String token) { this.token = token; }
}

