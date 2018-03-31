/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service.implementation;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lateral.lateral.annotation.ElasticSearchType;
import com.lateral.lateral.model.BaseEntity;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.BaseService;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.searchbox.client.JestClient;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

// TODO: BUG: Need to ensure update/post/delete calls are complete before returning to caller!!!!

/**
 * Represents a service class to handle a specified type of object
 * @param <T> The type of the object handled.
 */
public class DefaultBaseService<T extends BaseEntity> implements BaseService<T> {

    private static JestClient jestClient;
    // Stores T.class since java doesn't let you call T.class
    private final Class<T> typeArgument;

    protected final Gson gson;

    /**
     * Constructor for the service
     */
    protected DefaultBaseService(){
        // Source: https://stackoverflow.com/questions/3403909/
        ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
        this.typeArgument = (Class<T>)type.getActualTypeArguments()[0];

        gson = buildGson().create();
    }

    /**
     * Create GsonBuilder
     * Sublasses can override to extend the builder
     * @return builder
     */
    protected GsonBuilder buildGson() {
        return new GsonBuilder().serializeNulls();
    }

    /**
     * Verifies that the connection settings are correct
     */
    public static void verifySettings() {
        if (jestClient == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            jestClient = (JestDroidClient) factory.getObject();
        }
    }


    /**
     * Get the an item by the Jest ID of the object
     * @param id The Jest ID of the object
     * @return The object from the database
     */
    @Override
    public T getById(String id){
        String json = "{\"query\": {\"match\": {\"_id\": \"" + id + "\"}}}";
        return gson.fromJson(get(json), typeArgument);
    }

    /**
     * Gets the server's response based on the supplied query
     * @param query The query for the server
     * @return The server's response
     */
    @Override
    public String get(String query){
        String data = null;
        String ElasticSearchType = getElasticSearchType();
        GetData getData = new GetData(ElasticSearchType);

        getData.execute(query);
        try{
            data = getData.get();
        }catch(Exception e){
            Log.i("Error", "Failed to get task from async object");
        }
        return data;
    }

    /**
     * Pushes a new object to the database
     * @param obj Object to push
     */
    @Override
    public void post(T obj){
        String ElasticSearchType = getElasticSearchType();
        PostData postData = new PostData(ElasticSearchType);
        String id = null;

        String json = gson.toJson(obj);
        Log.i("json", json);


        postData.execute(json);
        try{
            id = postData.get();
        }catch(Exception e){
            Log.i("Error", "Failed to get task from async object");
            // TODO: Need to rethrow custom exception
        }
        obj.setId(id);
        setDocId(id);

    }

    /**
     * Updates an object currently in the database
     * @param obj Object to be updated
     */
    @Override
    public void update(T obj) {
        String ElasticSearchType = getElasticSearchType();
        UpdateData updateData = new UpdateData(ElasticSearchType, obj.getId());
        String id = null;

        String json = gson.toJson(obj);

        updateData.execute("{\"doc\": " + json + "}");

        try{
            id = updateData.get();
        } catch (Exception e){
            Log.i("Error", "Failed to get task from async object");
            // TODO: Need to rethrow custom exception
        }
    }


    /**
     * Sets the ID of the object into its source for retrieval/deserialization purposes
     * @param id The ID to set
     */
    public void setDocId(String id){
        String ElasticSearchType = getElasticSearchType();
        UpdateData updateData = new UpdateData(ElasticSearchType, id);
        String testId = null;
        String getIdJson = "{\"doc\": {\"id\": \"" + id + "\"}}";

        updateData.execute(getIdJson);
        try{
            testId = updateData.get();
        }catch(Exception e){
            Log.i("Error", "Failed to get task from async object");
        }

    }

    /**
     * Deletes an object based on its Jest ID
     * @param id The Jest ID of the object to be deleted
     */
    @Override
    public void delete(String id){
        String ElasticSearchType = getElasticSearchType();
        DeleteData deleteData = new DeleteData(ElasticSearchType);

        deleteData.execute(id);
        try{
            String success = deleteData.get();
        }catch(Exception e){
            Log.i("Error", "Failed to get task from async object");
            // TODO: Need to rethrow custom exception
        }
    }

    /**
     * Get the Index for elastic search from the {@link ElasticSearchType}
     * annotation on the type {@link T}
     * @return index name
     */
    protected final String getElasticSearchType(){
        Annotation annotation = typeArgument.getAnnotation(ElasticSearchType.class);

        return ((ElasticSearchType) annotation).Name();
    }

    /**
     * Gets extra value from JSON string based on the passed key
     * @param json The JSON string
     * @param key The key corresponding to the value
     * @return The value associated with the key
     */
    public String getValueFromJson(String json, String key){
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        return jsonObject.get(key).getAsString();
    }

    /**
     * AsyncTask to post an object
     */
    public static class PostData extends AsyncTask<String, Void, String> {
        String idx;

        /**
         * Constructor for the task
         * @param idx Type of the object
         */
        PostData(String idx){
            this.idx = idx;
        }


        /**
         * To do in the background of the AsyncTask
         * @param objs Objects to be posted
         * @return The ID of the posted object
         */
        @Override
        protected String doInBackground(String... objs) {
            verifySettings();

            String id = null;

            for(String obj: objs) {
                Index index = new Index.Builder(obj).index("cmput301w18t13").type(idx).build();

                try {
                    DocumentResult result = jestClient.execute(index);
                    if (result.isSucceeded()) {
                        Log.i("Success", "Post was succesful");
                        id = result.getId();
                    } else {
                        Log.i("Error", "A error occured");
                    }
                } catch (Exception e) {
                    Log.i("Error", "Application failed to send user to server");
                    // TODO: Need to rethrow custom exception
                }
            }
            return id;
        }
    }

    /**
     * AsyncTask to get data from the server
     */
    public static class GetData extends AsyncTask<String, Void, String> {
        String idx;

        /**
         * Constructor for the task
         * @param idx Type of the object
         */
        GetData(String idx){
            this.idx = idx;
        }

        /**
         * To do in the background of the AsyncTask
         * @param search_parameters Parameters for the search
         * @return The response of the server
         */
        @Override
        protected String doInBackground(String... search_parameters) {
            verifySettings();

            String get = null;

            /*
            attempt to to build a search and execute it, creating object from returned json string
             */
            Search search = new Search.Builder(search_parameters[0]).addIndex("cmput301w18t13").addType(idx).build();
            try {
                SearchResult result = jestClient.execute(search);
                if (result.isSucceeded()) {
                    get = result.getSourceAsString();
                } else {
                    Log.i("Error", "Search failed to return anything");
                }
            } catch (Exception e) {
                Log.i("Error", "Error communicating with the elasticsearch server!");
                // TODO: Need to rethrow custom exception
            }

            return get;
        }
    }


    /**
     * Async task to update an item in the database
     */
    public static class UpdateData extends AsyncTask<String, Void, String> {
        String idx;
        String id;

        /**
         * Constructor for the task
         * @param idx Type of the object
         * @param id ID of the object to updated
         */
        UpdateData(String idx, String id) {
            this.idx = idx;
            this.id = id;
        }

        /**
         * To do in the background of the AsyncTask
         * @param updateJson JSON query to perform the update
         * @return The ID of updated object
         */
        @Override
        protected String doInBackground(String... updateJson) {
            verifySettings();

            Update update = new Update.Builder(updateJson[0]).index("cmput301w18t13").type(idx).id(id).build();

            try {
                DocumentResult result = jestClient.execute(update);
                if (result.isSucceeded()) {
                    Log.i("Success", "Update was succesful");
                    id = result.getId();
                } else {
                    Log.i("Error", "A error occured");
                }
            } catch (Exception e) {
                Log.i("Error", "Application failed to send user to server");
                // TODO: Need to rethrow custom exception
            }
            return id;
        }

    }

    /**
     * AsyncTask to delete an item by ID
     */
    public static class DeleteData extends AsyncTask<String, Void, String> {
        String idx;

        /**
         * Constructor
         * @param idx Type of the object
         */
        DeleteData(String idx){
            this.idx = idx;
        }

        /**
         * To do in the background of the AsyncTask
         * @param obj Object to be deleted
         * @return Always returns "Success"
         */
        @Override
        protected String doInBackground(String... obj) {
            verifySettings();

                Delete delete = new Delete.Builder(obj[0]).index("cmput301w18t13").type(idx).build();

                try {
                    DocumentResult result = jestClient.execute(delete);
                    if (result.isSucceeded()) {
                        Log.i("Success", "Delete was succesful");
                    } else {
                        Log.i("Error", "A error occured");
                    }
                } catch (Exception e) {
                    Log.i("Error", "Application failed to send user to server");
                    // TODO: Need to rethrow custom exception
                }
            return "Success";
        }
    }

}



