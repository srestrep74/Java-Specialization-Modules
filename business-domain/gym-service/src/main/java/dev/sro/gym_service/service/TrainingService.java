package dev.sro.gym_service.service;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.DeleteTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.TraineeTrainingFilter;
import dev.sro.gym_service.dtos.v1.request.training.TraineeTrainingResponse;
import dev.sro.gym_service.dtos.v1.request.training.TrainerTrainingFilter;
import dev.sro.gym_service.dtos.v1.request.training.TrainerTrainingResponse;
import dev.sro.gym_service.dtos.v1.request.training.UpdateTrainingRequest;
import dev.sro.gym_service.dtos.v1.response.training.TrainingSummaryResponse;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;

import java.util.List;

public interface TrainingService {
    TrainerWorkloadResponse saveWithoutValidation(CreateTrainingRequest createTrainingRequest);

    TrainerWorkloadResponse saveWithValidation(CreateTrainingRequest createTrainingRequest);

    TrainingSummaryResponse findById(Long id);

    List<TrainingSummaryResponse> findAll();

    void deleteById(Long id);

    TrainerWorkloadResponse deleteTraining(DeleteTrainingRequest deleteTrainingRequest);

    TrainingSummaryResponse update(UpdateTrainingRequest updateTrainingRequest);

    TrainerWorkloadResponse updateTraining(UpdateTrainingRequest updateTrainingRequest);

    List<TraineeTrainingResponse> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO, String sortField,
            String sortDirection);

    List<TrainerTrainingResponse> findTrainingsByTrainerWithFilters(TrainerTrainingFilter filterDTO);
}
