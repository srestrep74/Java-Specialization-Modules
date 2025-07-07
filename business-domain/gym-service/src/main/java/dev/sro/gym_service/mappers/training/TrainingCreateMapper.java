package dev.sro.gym_service.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingCreateMapper {
    
    @Mapping(target = "trainingDate", source = "createTrainingRequest.trainingDate")
    @Mapping(target = "duration", source = "createTrainingRequest.trainingDuration")
    @Mapping(target = "trainingName", source = "createTrainingRequest.trainingName")
    @Mapping(target = "trainer", source = "trainer")
    @Mapping(target = "trainee", source = "trainee")
    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(target = "id", ignore = true)
    Training toEntity(CreateTrainingRequest createTrainingRequest, Trainer trainer, Trainee trainee, TrainingType trainingType);

}
