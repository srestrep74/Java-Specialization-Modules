package dev.sro.gym_service.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.response.training.TrainingSummaryResponse;
import dev.sro.gym_service.entity.Training;

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
