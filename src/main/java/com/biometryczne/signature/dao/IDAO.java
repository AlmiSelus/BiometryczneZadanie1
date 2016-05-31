package com.biometryczne.signature.dao;

import java.util.List;

/**
 * Created by Almi on 2016-05-31.
 */
public interface IDAO<T> {
    void create(T entity);
    List<T> getAll();
    void update(T entity);
    T remove(int id);
    T getById(int id);
}
