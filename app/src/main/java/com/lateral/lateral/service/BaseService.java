/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import com.lateral.lateral.model.BaseEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Represents a ElasticSearch service class for a certain type of data
 * @param <T> The class to be pushed
 */
public interface BaseService<T extends BaseEntity> {

    /**
     * Get a response from the server based on a query
     * @param query The query for the server
     * @return The server's response
     */
    String get(String query);

    /**
     * Returns an object based on its Jest ID
     * @param id The Jest ID of the object
     * @return The object from the database
     */
    T getById(String id);

    /**
     * Pushes a certain object to the database
     * @param obj Object to push
     */
    void post(T obj);

    /**
     * Deletes a certain object based on its Jest ID
     * @param id The Jest ID of the object to be deleted
     */
    public void delete(String id);

    /**
     * Updates the instance of a given object in the database
     * @param obj Object to be updated
     */
    public void update(T obj);
}
