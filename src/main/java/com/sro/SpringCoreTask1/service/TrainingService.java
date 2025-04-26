package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.UpdateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.training.TrainingSummaryResponse;

import java.util.List;

public interface TrainingService {
    void save(CreateTrainingRequest createTrainingRequest);

    TrainingSummaryResponse findById(Long id);

    List<TrainingSummaryResponse> findAll();

    void deleteById(Long id);

    TrainingSummaryResponse update(UpdateTrainingRequest updateTrainingRequest);

    List<TraineeTrainingResponse> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO, String sortField,
            String sortDirection);

    List<TrainerTrainingResponse> findTrainingsByTrainerWithFilters(TrainerTrainingFilter filterDTO);
}
