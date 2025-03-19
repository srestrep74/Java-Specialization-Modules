package com.sro.SpringCoreTask1.facade;

import java.util.List;
import java.util.Set;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;

public interface TraineeServiceFacade {
    TraineeResponseDTO createTrainee(TraineeRequestDTO traineeRequestDTO);
    TraineeResponseDTO findTraineeByUsername(String username);
    TraineeResponseDTO findTraineeById(Long id);
    boolean updateTraineePassword(Long traineeId, String newPassword);
    TraineeResponseDTO updateTrainee(TraineeRequestDTO traineeRequestDTO);
    void toggleTraineeStatus(Long traineeId);
    void deleteTraineeByUsername(String username);
    List<TrainerResponseDTO> findUnassignedTrainers(String traineeUsername);
    void updateTraineeTrainers(Long traineeId, Long trainerId, String action);
    Set<TrainerResponseDTO> findTraineeTrainers(Long traineeId);
    List<TrainingResponseDTO> findTraineeTrainings(String traineeUsername, TraineeTrainingFilterDTO filterDTO);
}
