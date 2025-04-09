package com.sro.SpringCoreTask1.service;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;

public interface TrainerService {
    TrainerResponseDTO save(TrainerRequestDTO dto);
    RegisterTrainerResponse saveFromAuth(RegisterTrainerRequest dto);
    TrainerResponseDTO findById(Long id);
    List<TrainerResponseDTO> findAll();
    void deleteById(Long id);
    TrainerProfileResponse update(String username, UpdateTrainerProfileRequest dto);
    TrainerProfileResponse findByUsername(String username);
    List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String traineeUsername);
    void toggleTrainerStatus(Long trainerId);
    boolean updateTrainerPassword(Long trainerId, String newPassword);
    Set<TrainerResponseDTO> findTrainersByTraineeId(Long traineeId);
}