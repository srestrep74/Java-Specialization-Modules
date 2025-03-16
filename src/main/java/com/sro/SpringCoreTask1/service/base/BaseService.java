package com.sro.SpringCoreTask1.service.base;

import java.util.List;


public interface BaseService<Request, Response, ID> {
    Response save(Request dto);
    Response findById(ID id);
    List<Response> findAll();
    void deleteById(ID id);
    Response update(Request dto);
}
