package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;

import java.util.List;

public interface TrainingService {
    TrainingResponseDTO save(TrainingRequestDTO trainingRequestDTO);
    TrainingResponseDTO findById(Long id);
    List<TrainingResponseDTO> findAll();
    void deleteById(Long id);
    TrainingResponseDTO update(TrainingRequestDTO trainingRequestDTO);
    
    List<TraineeTrainingResponse> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO);
    List<TrainerTrainingResponse> findTrainingsByTrainerWithFilters(TrainerTrainingFilter filterDTO);
    void addTraining(CreateTrainingRequest createTrainingRequest);
}
