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
import com.lateral.lateral.model.ServiceException;
import com.lateral.lateral.service.BaseService;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Represents a service class to handle a specified type of object
 * @param <T> The type of the object handled.
 */
public class DefaultBaseService<T extends BaseEntity> implements BaseService<T> {

    static protected final int RECORD_COUNT = 10000;
    static private String SERVER = "http://cmput301.softwareprocess.es:8080";
    static private String INDEX = "cmput301w18t13";


    private static JestClient jestClient;
    // Stores T.class since java doesn't let you call T.class
    private final Class<T> typeArgument;

    protected final Gson gson;


    /**
     * Constructor for the service
     */
    @SuppressWarnings("unchecked")
    protected DefaultBaseService(){
        // Source: https://stackoverflow.com/questions/3403909/
        ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
        Type arg = type.getActualTypeArguments()[0];
        this.typeArgument = (Class<T>)arg;

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
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(SERVER);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            jestClient = factory.getObject();
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

    private static boolean validateResult(JestResult result){
        return result.isSucceeded()
                // Success response codes
                && result.getResponseCode() >= 200
                && result.getResponseCode() < 300;
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
     * Get the an item by the Jest ID of the object
     * @param id The Jest ID of the object
     * @return The object from the database
     */
    @Override
    public T getById(String id){

        GetByIdData getData = new GetByIdData(getElasticSearchType());
        getData.execute(id);

        try {
            String data = getData.get();
            if (getData.serviceException != null)
                return null; // TODO: Testingthrow getData.serviceException;

            return gson.fromJson(data, typeArgument);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the server's response based on the supplied query
     * @param query The query for the server
     * @return The server's response
     */
    @Override
    public String search(String query) {

        SearchData getData = new SearchData(getElasticSearchType());
        getData.execute(query);

        try {
            String data = getData.get();
            if (getData.serviceException != null)
                return null; // TODO: Testingthrow getData.serviceException;

            return data;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pushes a new object to the database
     * @param obj Object to push
     */
    @Override
    public void post(T obj){

        PostData postData = new PostData(getElasticSearchType());
        postData.execute(gson.toJson(obj));

        try{
            String id = postData.get();
            if (postData.serviceException != null)
                return; // TODO: Testingthrow postData.serviceException;

            obj.setId(id);
            setDocId(id);

        } catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an object currently in the database
     * @param obj Object to be updated
     */
    @Override
    public void update(T obj) {

        UpdateData updateData = new UpdateData(getElasticSearchType(), obj.getId());
        updateData.execute("{\"doc\": " + gson.toJson(obj) + "}");

        try{
            updateData.get();
            if (updateData.serviceException != null)
                return; // TODO: Testing throw updateData.serviceException;

        } catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Sets the ID of the object into its source for retrieval/deserialization purposes
     * @param id The ID to set
     */
    public void setDocId(String id){

        UpdateData updateData = new UpdateData(getElasticSearchType(), id);
        String getIdJson = "{\"doc\": {\"id\": \"" + id + "\"}}";
        updateData.execute(getIdJson);

        try{
            updateData.get();
            if (updateData.serviceException != null)
                return; // TODO: Testingthrow updateData.serviceException;

        } catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an object based on its Jest ID
     * @param id The Jest ID of the object to be deleted
     */
    @Override
    public void delete(String id){

        DeleteData deleteData = new DeleteData(getElasticSearchType());
        deleteData.execute(id);

        try{
            deleteData.get();
            if (deleteData.serviceException != null)
                return; // TODO: Testingthrow deleteData.serviceException;

        } catch(InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * AsyncTask to post an object
     */
    public static class PostData extends AsyncTask<String, Void, String> {
        String idx;
        ServiceException serviceException = null;

        /**
         * Constructor for the task
         * @param idx Type of the object
         */
        PostData(String idx){
            this.idx = idx;
        }


        /**
         * To do in the background of the AsyncTask
         * @param obj Objects to be posted
         * @return The ID of the posted object
         */
        @Override
        protected String doInBackground(String... obj) {
            verifySettings();

            Index index = new Index.Builder(obj[0]).index(INDEX).type(idx).refresh(true).build();
            String id = null;

            try {
                DocumentResult result = jestClient.execute(index);
                if (validateResult(result)) {
                    id = result.getId();
                } else {
                    Log.e("Post Error Code", ((Integer)result.getResponseCode()).toString());
                    serviceException = new ServiceException("Post failed");
                }
            } catch(IOException e){
                Log.i("Post IOException", e.toString());
                serviceException = new ServiceException("Error communicating with the elasticsearch server!");
            }
            return id;
        }
    }

    /**
     * AsyncTask to get specific record from the server
     */
    public static class GetByIdData extends AsyncTask<String, Void, String> {
        private String idx;
        ServiceException serviceException = null;

        /**
         * Constructor for the task
         * @param idx Type of the object
         */
        GetByIdData(String idx){
            this.idx = idx;
        }

        /**
         * To do in the background of the AsyncTask
         * @param ids item ids
         * @return The response of the server
         */
        @Override
        protected String doInBackground(String... ids) {
            verifySettings();
            Get get = new Get.Builder(INDEX, ids[0]).type(idx).build();
            String data = null;
            try{
                JestResult result = jestClient.execute(get);
                if (validateResult(result)){
                    data = result.getSourceAsString();
                } else {
                    Log.e("Get Error Code", ((Integer)result.getResponseCode()).toString());
                    serviceException = new ServiceException("Get failed");
                }
            } catch(IOException e){
                Log.i("Get IOException", e.toString());
                serviceException = new ServiceException("Error communicating with the elasticsearch server!");
            }
            return data;
        }
    }

    /**
     * AsyncTask to search data from the server
     */
    public static class SearchData extends AsyncTask<String, Void, String> {
        private String idx;
        ServiceException serviceException = null;

        /**
         * Constructor for the task
         * @param idx Type of the object
         */
        SearchData(String idx){
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

            Search search = new Search.Builder(search_parameters[0]).addIndex(INDEX).addType(idx).build();
            String data = null;
            try{
                SearchResult result = jestClient.execute(search);
                if (validateResult(result)){
                    data = result.getSourceAsString();
                } else {
                    Log.e("Search Error Code", ((Integer)result.getResponseCode()).toString());
                    serviceException = new ServiceException("Search failed");
                }
            } catch(IOException e){
                Log.i("Search IOException", e.toString());
                serviceException = new ServiceException("Error communicating with the elasticsearch server!");
            }
            return data;
        }
    }


    /**
     * Async task to update an item in the database
     */
    public static class UpdateData extends AsyncTask<String, Void, String> {
        private String idx;
        private String id;
        ServiceException serviceException = null;

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

            Update update = new Update.Builder(updateJson[0]).index(INDEX).type(idx).id(id).refresh(true).build();

            try {
                DocumentResult result = jestClient.execute(update);
                if (validateResult(result)) {
                    id = result.getId();
                } else {
                    Log.e("Update Error Code", ((Integer)result.getResponseCode()).toString());
                    serviceException = new ServiceException("Update failed");
                }
            } catch(IOException e){
                Log.i("Update IOException", e.toString());
                serviceException = new ServiceException("Error communicating with the elasticsearch server!");
            }
            return id;
        }

    }

    /**
     * AsyncTask to delete an item by ID
     */
    public static class DeleteData extends AsyncTask<String, Void, String> {
        private String idx;
        ServiceException serviceException = null;

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

            Delete delete = new Delete.Builder(obj[0]).index(INDEX).type(idx).refresh(true).build();

            try {
                DocumentResult result = jestClient.execute(delete);
                if (!validateResult(result)) {
                    Log.e("Delete Error Code", ((Integer)result.getResponseCode()).toString());
                    serviceException = new ServiceException("Delete failed");
                }
            } catch(IOException e){
                Log.i("Delete IOException", e.toString());
                serviceException = new ServiceException("Error communicating with the elasticsearch server!");
            }
            return null;
        }
    }
}



