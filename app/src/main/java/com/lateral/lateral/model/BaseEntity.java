/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import io.searchbox.annotations.JestId;

/**
 * Represents an element that will be pushed to the ElasticSearch server
 */
public abstract class BaseEntity {

    @JestId
    private String id;

    /**
     * Gets the ID of the object
     * @return The object's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the object
     * @param id The ID to be set to
     */
    public void setId(String id){
        this.id = id;
    }
}
