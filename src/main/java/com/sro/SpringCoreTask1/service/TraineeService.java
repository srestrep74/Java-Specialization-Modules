package com.sro.SpringCoreTask1.service;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.entity.Trainer;

public interface TraineeService {
    TraineeResponseDTO save(TraineeRequestDTO traineeRequestDTO);
    RegisterTraineeResponse saveFromAuth(RegisterTraineeRequest traineeRegistrationRequestDTO);
    TraineeResponseDTO findById(Long id);
    List<TraineeResponseDTO> findAll();
    TraineeProfileResponse update(String username, UpdateTraineeProfileRequest traineeUpdateRequestDTO);
    void deleteById(Long id);
    TraineeProfileResponse findByUsername(String username);
    void deleteByUsername(String username);
    void addTrainerToTrainee(Long traineeId, Long trainerId);
    void removeTrainerFromTrainee(Long traineeId, Long trainerId);
    void setTraineeStatus(Long traineeId);
    boolean updateTraineePassword(Long traineeId, String newPassword);
    Set<Trainer> findTrainersByTraineeId(Long traineeId);
}
