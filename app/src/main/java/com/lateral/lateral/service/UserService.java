/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import com.lateral.lateral.model.User;

/**
 * Interface for interacting with Users on the ElasticSearch server
 */
public interface UserService extends BaseService<User>{

    User getUserByUsername(String username);


    /**
     * Get user by User ID (The one created by elastic search
     * @param ID The ID of the user
     * @return The User with this ID if it exists; null otherwise
     */
    User getUserByID(String ID);


    /**
     * Retrieve the salt-and-hash of a user based on the username
     * @param username Username of the user
     * @return The salt-and-hash of the user if it exists; null otherwise
     */
    String getSaltAndHash(String username);

    /**
     * Gets the user's ID based on the user's username
     * @param username Username of the user
     * @return The ID of the user if it exists; null otherwise
     */
    String getIdByUsername(String username);


    /**
     * Gets the user's token based on the user's username
     * @param id ID of the user
     * @return The token of the user if it exists; null otherwise;
     */
    String getToken(String id);
}
