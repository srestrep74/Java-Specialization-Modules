package com.sro.SpringCoreTask1.facade;

import java.util.List;

import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;

public interface TrainerServiceFacade {
    TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO);
    TrainerResponseDTO findTrainerByUsername(String username);
    TrainerResponseDTO findTrainerById(Long id);
    boolean updateTrainerPassword(Long trainerId, String newPassword);
    TrainerResponseDTO updateTrainer(TrainerRequestDTO trainerRequestDTO);
    void toggleTrainerStatus(Long trainerId);
    List<TrainingResponseDTO> findTrainerTrainings(String trainerUsername, TrainerTrainingFilterDTO filterDTO);
}
