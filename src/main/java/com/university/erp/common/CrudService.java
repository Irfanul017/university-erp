package com.university.erp.common;

import java.util.List;

public interface CrudService<T, ID> {
    T create(T entity);

    List<T> findAll();

    T findById(ID id);

    T update(ID id, T entity);

    void delete(ID id);
}
