package dev.sro.gym_service.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.training.TrainerTrainingResponse;
import dev.sro.gym_service.entity.Training;

@Mapper(componentModel = "spring")
public interface TraininigTrainerMapper {
    
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "duration", target = "trainingDuration")
    @Mapping(source = "trainee.username", target = "traineeName")
    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    TrainerTrainingResponse toTrainerTrainingResponse(Training training);

}
