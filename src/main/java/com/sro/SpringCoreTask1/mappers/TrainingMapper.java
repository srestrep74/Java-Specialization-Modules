package com.sro.SpringCoreTask1.mappers;

import com.sro.SpringCoreTask1.dto.TrainingDTO;
import com.sro.SpringCoreTask1.models.Training;
import com.sro.SpringCoreTask1.models.id.TrainingId;

public class TrainingMapper {

    public static TrainingDTO toDTO(Training training) {
        if (training == null) {
            return null;
        }

        return new TrainingDTO(
            training.getTrainingId(),
            training.getTrainingName(),
            training.getTrainingDate(),
            training.getDuration(),
            TraineeMapper.toDTO(training.getTrainee()),  
            TrainerMapper.toDTO(training.getTrainer()),  
            training.getTrainingType()
        );
    }

    public static Training toEntity(TrainingDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Training(
            new TrainingId(dto.getTrainee().getUserId(), dto.getTrainer().getUserId()), 
            dto.getTrainingName(),
            dto.getTrainingDate(),
            dto.getDuration(),
            TraineeMapper.toEntity(dto.getTrainee()),  
            TrainerMapper.toEntity(dto.getTrainer()), 
            dto.getTrainingType()
        );
    }
}
