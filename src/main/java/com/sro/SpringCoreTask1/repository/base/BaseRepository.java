package com.sro.SpringCoreTask1.repository.base;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {

    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    Optional<T> update(T entity);
    
}
