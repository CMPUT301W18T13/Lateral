/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;

import java.util.ArrayList;

/**
 * Interface for interacting with Tasks on the ElasticSearch server
 */
public interface TaskService extends BaseService<Task> {

    ArrayList<Task> getAllTasksByRequesterID(String requesterID) throws ServiceException;
}
