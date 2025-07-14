package dev.sro.gym_service.service;

import java.util.List;
import java.util.Set;

import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import dev.sro.gym_service.dtos.v1.response.trainer.RegisterTrainerResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.TrainerProfileResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.UnassignedTrainerResponse;

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