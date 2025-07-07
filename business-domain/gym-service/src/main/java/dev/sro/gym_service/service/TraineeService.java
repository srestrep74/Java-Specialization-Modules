package dev.sro.gym_service.service;

import java.util.List;
import java.util.Set;

import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.UpdateTraineeTrainerListRequest;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.TraineeProfileResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.TrainerSummaryResponse;
import dev.sro.gym_service.entity.Trainer;

public interface TraineeService {
    RegisterTraineeResponse save(RegisterTraineeRequest traineeRegistrationRequestDTO);

    TraineeProfileResponse findById(Long id);

    List<TraineeProfileResponse> findAll();

    TraineeProfileResponse update(String username, UpdateTraineeProfileRequest traineeUpdateRequestDTO);

    void deleteById(Long id);

    TraineeProfileResponse findByUsername(String username);

    void deleteByUsername(String username);

    void addTrainerToTrainee(Long traineeId, Long trainerId);

    void removeTrainerFromTrainee(Long traineeId, Long trainerId);

    void updateActivationStatus(String username, boolean active);

    boolean updateTraineePassword(Long traineeId, String newPassword);

    Set<Trainer> findTrainersByTraineeId(Long traineeId);

    List<TrainerSummaryResponse> updateTraineeTrainers(String username,
            UpdateTraineeTrainerListRequest updateTrainersRequest);
}
