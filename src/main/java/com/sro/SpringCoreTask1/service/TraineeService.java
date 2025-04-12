package com.sro.SpringCoreTask1.service;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeTrainerListRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponse;
import com.sro.SpringCoreTask1.entity.Trainer;

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
    List<TrainerSummaryResponse> updateTraineeTrainers(String username, UpdateTraineeTrainerListRequest updateTrainersRequest);
}
