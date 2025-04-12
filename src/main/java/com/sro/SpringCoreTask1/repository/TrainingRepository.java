package com.sro.SpringCoreTask1.repository;


import java.time.LocalDate;
import java.util.List;

import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TrainingRepository extends BaseRepository<Training, Long> {
    List<Training> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO, String sortField, String sortDirection);
    List<Training> findTrainingsByTrainerWithFilters(TrainerTrainingFilter filterDTO);
    boolean existsByTraineeIdAndTrainerAndTrainingDate(Trainee trainee, Trainer traineer, LocalDate trainingDate);
}
