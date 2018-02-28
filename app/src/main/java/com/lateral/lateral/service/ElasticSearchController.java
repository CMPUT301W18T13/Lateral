package com.lateral.lateral.service;

import android.os.AsyncTask;
import android.util.Log;

import com.lateral.lateral.model.User;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.LinkedHashMap;
import java.util.Map;

import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;

/*
Controller to handle elasticsearch queries
 */
public class ElasticSearchController {

    private static JestClient jestClient;

    /*
    method to add a user into the server via elasticsearch
     */
    public static class AddUserTask extends AsyncTask<User, Void, Void>{

        @Override
        protected Void doInBackground(User... users){
            verifySettings();

            /*
            mapping function to create JSON string
             */
            for(User user: users){
                Map<String, String> addUser = new LinkedHashMap<String, String>();
                addUser.put("username", user.getUsername());
                addUser.put("phoneNumber", user.getPhoneNumber());
                addUser.put("emailAddress", user.getEmailAddress());

                Index index = new Index.Builder(addUser).index("cmput301w18t13").type("user").build();

                try{
                    DocumentResult result = jestClient.execute(index);
                    if(result.isSucceeded()){
                        user.setId(result.getId());
                    }else{
                        Log.i("Error", "A error occured");
                    }
                }catch (Exception e){
                    Log.i("Error", "Application failed to send user to server");
                }
            }
            return null;
        }
    }

    public static void verifySettings(){
        if(jestClient == null){
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            jestClient = (JestDroidClient) factory.getObject();
        }
    }
}
