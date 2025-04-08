package com.sro.SpringCoreTask1.service;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.TraineeRegistrationRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.TraineeUpdateRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.TraineeRegistrationResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainer;

public interface TraineeService {
    TraineeResponseDTO save(TraineeRequestDTO traineeRequestDTO);
    TraineeRegistrationResponse saveFromAuth(TraineeRegistrationRequest traineeRegistrationRequestDTO);
    TraineeResponseDTO findById(Long id);
    List<TraineeResponseDTO> findAll();
    TraineeProfileResponseDTO update(String username, TraineeUpdateRequestDTO traineeUpdateRequestDTO);
    void deleteById(Long id);
    TraineeProfileResponseDTO findByUsername(String username);
    void deleteByUsername(String username);
    void addTrainerToTrainee(Long traineeId, Long trainerId);
    void removeTrainerFromTrainee(Long traineeId, Long trainerId);
    void setTraineeStatus(Long traineeId);
    boolean updateTraineePassword(Long traineeId, String newPassword);
    Set<Trainer> findTrainersByTraineeId(Long traineeId);
}
