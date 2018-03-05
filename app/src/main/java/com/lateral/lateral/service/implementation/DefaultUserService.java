package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.lateral.lateral.model.User;
import com.lateral.lateral.service.ElasticSearchController;
import com.lateral.lateral.service.UserService;

import java.lang.reflect.Type;

public class DefaultUserService extends DefaultBaseService<User>{

    public User getUserByUsername(String username){
        String json = "{\"query\": {\"match\": {\"username\": \"" + username + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }

    public User getUserById(String id){
        String json = "{\"query\": {\"match\": {\"_id\": \"" + id + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }

    public void postUser(User user){
        post(gson.toJson(user, User.class));
    }


    // TODO: Add extra methods specific to the User index

}
