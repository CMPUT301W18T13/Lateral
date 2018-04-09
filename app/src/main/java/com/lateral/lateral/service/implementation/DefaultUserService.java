/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.UserService;

/**
 * Service for interfacing with Users on the ElasticSearch server
 * Note; any failure to return results will return null, so check accordingly
 */
public class DefaultUserService extends DefaultBaseService<User> implements UserService{

    /**
     * Gets the user from the database based on the given username
     * @param username Username of the user
     * @return The User with this username if it exists; null otherwise
     */
    public User getUserByUsername(String username) throws ServiceException {
        String json = "{\"query\": {\"match\": {\"username\": \"" + username + "\"}}}";
        return gson.fromJson(search(json), User.class);
    }
}
