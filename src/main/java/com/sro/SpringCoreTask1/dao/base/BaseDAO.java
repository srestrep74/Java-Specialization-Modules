package com.sro.SpringCoreTask1.dao.base;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    T update(T entity);
}
