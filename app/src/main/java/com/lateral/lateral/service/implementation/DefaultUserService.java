package com.lateral.lateral.service.implementation;

import android.util.Log;

import com.lateral.lateral.model.User;
import com.lateral.lateral.service.ElasticSearchController;
import com.lateral.lateral.service.UserService;

import org.json.JSONArray;

import java.lang.reflect.Type;

/*
class to handle all user interactions with elasticsearch
failure to return any results will always return null, so check accordingly
 */
public class DefaultUserService extends DefaultBaseService<User>{

    /*
     get user by username from fb
      */
    public User getUserByUsername(String username){
        String json = "{\"query\": {\"match\": {\"username\": \"" + username + "\"}}}";
        return gson.fromJson(get(json), User.class);
    }

    /*
     get user by id
      */
    public User getUserById(String id){
        String json = "{\"query\": {\"match\": {\"_id\": \"" + id + "\"}}}";

        return gson.fromJson(get(json), User.class);
    }

    /*
     retrieve a users salt and hash
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

    /*
     retrieves users token from db
      */
    public String getToken(String username){
        String json = "{\"_source\": [\"token\"], \"query\": {\"match\": {\"username\": \"" + username + "\"}}}";

        String result = get(json);

        if(result.equals("")){
            return null;
        }else {
            return getValueFromJson(result, "token");
        }

    }

    // TODO: Add extra methods specific to the User index

}
