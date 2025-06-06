package com.sro.SpringCoreTask1.service;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;

public interface TrainerService {
    RegisterTrainerResponse save(RegisterTrainerRequest dto);

    TrainerProfileResponse findById(Long id);

    List<TrainerProfileResponse> findAll();

    void deleteById(Long id);

    TrainerProfileResponse update(String username, UpdateTrainerProfileRequest dto);

    TrainerProfileResponse findByUsername(String username);

    List<UnassignedTrainerResponse> findUnassignedTrainersByTraineeUsername(String traineeUsername);

    void updateActivationStatus(String username, boolean active);

    boolean updateTrainerPassword(Long trainerId, String newPassword);

    Set<TrainerProfileResponse> findTrainersByTraineeId(Long traineeId);
}