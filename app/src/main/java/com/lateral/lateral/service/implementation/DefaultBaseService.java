package com.lateral.lateral.service.implementation;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.lateral.lateral.annotation.ElasticSearchType;
import com.lateral.lateral.model.BaseEntity;
import com.lateral.lateral.model.User;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.apache.commons.lang3.NotImplementedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class DefaultBaseService {
    // TODO: Change to actual URL for project
    //private String URL = "temporaryURL";

    // Stores T.class since java doesn't let you call T.class
    //private final Class<?> typeArgument;

    private static JestClient jestClient;

    public static class GetData extends AsyncTask<String, Void, String> {
        String idx;

        GetData(String idx){
            this.idx = idx;
        }


        @Override
        protected String doInBackground(String... search_parameters) {
            verifySettings();

            String get = null;
            Gson gson = new Gson();

            /*
            attempt to to build a search and execute it, creating object from returned json string
             */
            Search search = new Search.Builder(search_parameters[0]).addIndex("cmput301w18t13").addType(idx).build();
            try {
                SearchResult result = jestClient.execute(search);
                if (result.isSucceeded()) {
                    //get = gson.fromJson(result.getSourceAsString(), User.class);
                    get = result.getSourceAsString();
                } else {
                    Log.i("Error", "Search failed to return anything");
                }
            } catch (Exception e) {
                Log.i("Error", "Error communicating with the elasticsearch server!");
            }

            return get;

        }
    }

    public static void verifySettings() {
        if (jestClient == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            jestClient = (JestDroidClient) factory.getObject();
        }
    }

    public String get(String idx){

        String data = null;
        GetData getData = new GetData(idx);

        getData.execute("");
        try{
            data = getData.get();
        }catch(Exception e){
            Log.i("Error", "Failed to get task from async object");
        }
        return data;
    }
}

    //protected DefaultBaseService(I input, O output){
        // Get the typeArgument of T
        // Source: stackoverflow.com/questions/30533194
        //ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
        //this.typeArgument = (Class<?>)type.getActualTypeArguments()[0];
        //this.input = input;
        //this.output = output;
    //}


    /**
     * Get the Index for elastic search from the {@link ElasticSearchType}
     * annotation on the type {@link T}
     * @return index name
     */
    //protected final String getElasticSearchType(){
        //Annotation annotation = typeArgument.getAnnotation(ElasticSearchType.class);
        //if (annotation == null){
            // TODO: Throw some error. Class needs annotation
        //}

        //return ((ElasticSearchType) annotation).Name();
    //}

   //


