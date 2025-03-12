package com.sro.SpringCoreTask1.repository;


import java.util.List;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TrainingRepository extends BaseRepository<Training, Long> {
    List<Training> findTrainingsByTraineeWithFilters(TraineeTrainingFilterDTO filterDTO);
    List<Training> findTrainingsByTrainerWithFilters(TrainerTrainingFilterDTO filterDTO);
}
