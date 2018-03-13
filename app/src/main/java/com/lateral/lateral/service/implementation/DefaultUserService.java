package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.lateral.lateral.model.User;
import com.lateral.lateral.service.ElasticSearchController;
import com.lateral.lateral.service.UserService;

import java.lang.reflect.Type;

public class DefaultUserService extends DefaultBaseService<User>{

    // get user by username
    public User getUserByUsername(String username){
        String json = "{\"query\": {\"match\": {\"username\": \"" + username + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }

    // get user by id
    public User getUserById(String id){
        String json = "{\"query\": {\"match\": {\"_id\": \"" + id + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }

    // retrieve a users salt and hash
    public String getSaltAndHash(String username){
        String json = "{\"_source\": [\"saltAndHash\"] \"query\": {\"match\": {\"username\": \"" + username + "\"}}}";
        return get(json);
    }



    // TODO: Add extra methods specific to the User index

}
