package com.sro.SpringCoreTask1.service;


import java.util.List;
import java.util.Optional;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.service.base.BaseService;

public interface TrainerService extends BaseService<TrainerRequestDTO, TrainerResponseDTO, Long> {
    Optional<TrainerResponseDTO> findByUsername(String username);
    List<TrainerResponseDTO> getTrainersNotAssignedToTrainee(String traineeUsername);
}
