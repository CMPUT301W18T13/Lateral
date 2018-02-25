package com.lateral.lateral.model;


import io.searchbox.annotations.JestId;

public abstract class BaseEntity {

    @JestId
    private String documentId;

    public String getId() {
        return documentId;
    }

    public void setId(String id){
        this.documentId = id;
    }
}
