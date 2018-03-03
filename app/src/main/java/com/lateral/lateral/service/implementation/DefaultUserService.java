package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.lateral.lateral.model.User;
import com.lateral.lateral.service.ElasticSearchController;
import com.lateral.lateral.service.UserService;

public class DefaultUserService extends DefaultBaseService{

    public String getData(){
        String type = "User";
        return super.get(type);
    }


    // TODO: Add extra methods specific to the User index

}
