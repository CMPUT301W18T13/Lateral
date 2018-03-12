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
import java.lang.reflect.Type;
import java.util.List;

import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class DefaultBaseService<T extends BaseEntity> {
    // Stores T.class since java doesn't let you call T.class
    //private final Class<?> typeArgument;

    private static JestClient jestClient;
    private final Class<?> typeArgument;

    Gson gson = new Gson();

    protected DefaultBaseService(){
        // https://stackoverflow.com/questions/3403909/get-generic-type-of-class-at-runtime
        ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
        this.typeArgument = (Class<?>)type.getActualTypeArguments()[0];
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


    public void post(T obj){
        String ElasticSearchType = getElasticSearchType();
        PostData postData = new PostData(ElasticSearchType);
        String id = null;

        String json = gson.toJson(obj);

        postData.execute(json);
        try{
            id = postData.get();
        }catch(Exception e){
            Log.i("Error", "Failed to get task from async object");
        }
        obj.setId(id);
    }

    /**
     * Get the Index for elastic search from the {@link ElasticSearchType}
     * annotation on the type {@link T}
     * @return index name
     */
    protected final String getElasticSearchType(){
        Annotation annotation = typeArgument.getAnnotation(ElasticSearchType.class);
        if (annotation == null){
            // TODO: Throw some error. Class needs annotation
        }

        return ((ElasticSearchType) annotation).Name();
    }

    public static class PostData extends AsyncTask<String, Void, String> {
        String idx;

        PostData(String idx){
            this.idx = idx;
        }


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
                }
            }
            return id;
        }
    }

    public static class GetData extends AsyncTask<String, Void, String> {
        String idx;

        GetData(String idx){
            this.idx = idx;
        }

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
            }

            return get;
        }
    }

}



