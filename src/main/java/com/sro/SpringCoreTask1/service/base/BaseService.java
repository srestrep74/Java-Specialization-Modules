package com.sro.SpringCoreTask1.service.base;

import java.util.List;
import java.util.Optional;


public interface BaseService<Request, Response, ID> {
    Response save(Request dto);
    Optional<Response> findById(ID id);
    List<Response> findAll();
    void deleteById(ID id);
    Response update(Request dto);
}
