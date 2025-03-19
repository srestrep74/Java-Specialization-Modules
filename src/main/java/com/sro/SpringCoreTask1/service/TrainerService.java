package com.sro.SpringCoreTask1.service;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.service.base.BaseService;

public interface TrainerService extends BaseService<TrainerRequestDTO, TrainerResponseDTO, Long> {
    TrainerResponseDTO findByUsername(String username);
    List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String traineeUsername);
    void toggleTrainerStatus(Long trainerId);
    boolean updateTrainerPassword(Long trainerId, String newPassword);
    Set<TrainerResponseDTO> findTrainersByTraineeId(Long traineeId);
}