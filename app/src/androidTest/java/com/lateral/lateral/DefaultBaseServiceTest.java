package com.lateral.lateral;

import android.support.test.runner.AndroidJUnit4;


import com.lateral.lateral.model.Task;
import com.lateral.lateral.service.implementation.DefaultBaseService;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

// TODO: instantiating Search service is a problem
@RunWith(AndroidJUnit4.class)
public class DefaultBaseServiceTest extends DefaultBaseService {

    @Test
    public void testPostingTask(){
        Task task = new Task("mow my lawn", "my lawn needs mowed and I want you to do it");
        post(task);
    }


}
