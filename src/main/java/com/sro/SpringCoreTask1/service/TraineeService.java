package com.sro.SpringCoreTask1.service;


import java.util.Optional;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.service.base.BaseService;

public interface TraineeService extends BaseService<TraineeRequestDTO, TraineeResponseDTO, Long>{
    Optional<TraineeResponseDTO> findByUsername(String username);
}
