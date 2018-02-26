package com.lateral.lateral.service;

import com.lateral.lateral.model.BaseEntity;

import java.util.List;

public interface BaseService<T extends BaseEntity> {
    void save(T item);

    void delete(T item);

    T getById(String id);

    List<T> getAll();
}
