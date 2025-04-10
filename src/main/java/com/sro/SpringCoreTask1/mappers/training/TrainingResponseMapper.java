package com.sro.SpringCoreTask1.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.response.training.TrainingSummaryResponse;
import com.sro.SpringCoreTask1.entity.Training;

@Mapper(componentModel = "spring")
public interface TrainingResponseMapper {
    
    @Mapping(source = "training.trainingName", target = "trainingName")
    @Mapping(source = "training.trainingDate", target = "trainingDate")
    @Mapping(source = "training.duration", target = "trainingDuration")
    @Mapping(source = "training.trainer.firstName", target = "trainerName")
    @Mapping(source = "training.trainee.firstName", target = "traineeName")
    @Mapping(source = "training.trainingType.trainingTypeName", target = "trainingType")
    TrainingSummaryResponse toTrainingSummaryResponse(Training training);

}
