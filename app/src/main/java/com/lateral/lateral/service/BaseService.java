package com.lateral.lateral.service;

import com.lateral.lateral.model.BaseEntity;

import java.util.List;

public interface BaseService<T extends BaseEntity> {

    String get(String query);

    T getById(String id);

    void post(T obj);
}
