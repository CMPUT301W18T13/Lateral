package com.lateral.lateral.model;


import io.searchbox.annotations.JestId;

public abstract class BaseEntity {

    @JestId
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
