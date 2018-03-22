/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.lateral.lateral.model.User;
import com.lateral.lateral.service.UserService;

import org.json.JSONArray;

import java.lang.reflect.Type;

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
    public User getUserByUsername(String username){
        String json = "{\"query\": {\"match\": {\"username\": \"" + username + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }


    /**
     * Get user by User ID (The one created by elastic search
     * @param ID The ID of the user
     * @return The User with this ID if it exists; null otherwise
     */
    public User getUserByID(String ID){
        String json = "{\"query\": {\"match\": {\"id\": \"" + ID + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }


    /**
     * Retrieve the salt-and-hash of a user based on the username
     * @param username Username of the user
     * @return The salt-and-hash of the user if it exists; null otherwise
     */
    public String getSaltAndHash(String username){
        String json = "{\"_source\": [\"saltAndHash\"], \"query\": {\"match\": {\"username\": \"" + username + "\"}}}";

        String result = get(json);

        if(result.equals("")){
            return null;
        }else {
            return getValueFromJson(result, "saltAndHash");
        }
    }

    /**
     * Gets the user's ID based on the user's username
     * @param username Username of the user
     * @return The ID of the user if it exists; null otherwise
     */
    public String getIdByUsername(String username){
        String json = "{\"_source\": [\"id\"], \"query\": {\"match\": {\"username\": \"" + username + "\"}}}";

        String result = get(json);

        if(result.equals("")){
            return null;
        }else {
            return getValueFromJson(result, "id");
        }
    }


    /**
     * Gets the user's token based on the user's username
     * @param id ID of the user
     * @return The token of the user if it exists; null otherwise;
     */
    public String getToken(String id){
        String json = "{\"_source\": [\"token\"], \"query\": {\"match\": {\"id\": \"" + id + "\"}}}";

        String result = get(json);

        if(result.equals("")){
            return null;
        }else {
            return getValueFromJson(result, "token");
        }

    }
}
