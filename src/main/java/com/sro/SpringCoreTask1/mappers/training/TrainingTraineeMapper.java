package com.sro.SpringCoreTask1.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.entity.Training;

@Mapper(componentModel = "spring")
public interface TrainingTraineeMapper {
    
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    @Mapping(source = "duration", target = "trainingDuration")
    @Mapping(source = "trainer.username", target = "trainerName")
    TraineeTrainingResponse toTraineeTrainingResponse(Training training);

}
