package com.sro.SpringCoreTask1.service;


import java.util.List;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.service.base.BaseService;

public interface TrainerService extends BaseService<TrainerRequestDTO, TrainerResponseDTO, Long> {
    TrainerResponseDTO findByUsername(String username);
    List<TrainerResponseDTO> getTrainersNotAssignedToTrainee(String traineeUsername);
    void setTrainerStatus(Long trainerId, boolean isActive);
}
