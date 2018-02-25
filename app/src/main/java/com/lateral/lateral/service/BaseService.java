package com.lateral.lateral.service;

import com.lateral.lateral.annotation.ServiceIndex;
import com.lateral.lateral.model.BaseEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

public abstract class BaseService<T extends BaseEntity> {
    // TODO: Change to actual URL for project
    private String URL = "temporaryURL";

    // Stores T.class since java doesn't let you call T.class
    private final Class<?> typeArgument;

    protected BaseService(){
        // Get the typeArgument of T
        // Source: stackoverflow.com/questions/30533194
        ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
        this.typeArgument = (Class<?>)type.getActualTypeArguments()[0];
    }


    /**
     * Get the Index for elastic search from the {@link ServiceIndex}
     * annotation on the type {@link T}
     * @return index name
     */
    protected final String getIndexName(){
        Annotation annotation = typeArgument.getAnnotation(ServiceIndex.class);
        if (annotation == null){
            // TODO: Throw some error. Class needs annotation
        }

        return ((ServiceIndex) annotation).Name();
    }

    public void save(T item){
        // TODO: Implement
    }

    public void delete(T item){
        // TODO: Implement
    }

    public void getById(String id){
        // TODO: Implement
    }

    public void getAll(){
        // TODO: Implement
    }

    // NOTE: Only call this from subclasses, filter string should be defined in service layer
    protected void getAll(String filter){
        // TODO: Implement
    }
}
