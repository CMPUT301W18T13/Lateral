/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

/**
 * Custom exception for service issues
 */
public class ServiceException extends Exception {

    /**
     * Create new ServiceException
     * @param message message
     */
    public ServiceException(String message) {
        super(message);
    }
    /**
     * Create new ServiceException
     * @param message message
     * @param cause cause
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
