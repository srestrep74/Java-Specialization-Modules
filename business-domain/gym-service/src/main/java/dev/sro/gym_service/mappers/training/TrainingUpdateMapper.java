package dev.sro.gym_service.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.training.UpdateTrainingRequest;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingUpdateMapper {
    
    @Mapping(target = "trainingDate", source = "updateTrainingRequest.trainingDate")
    @Mapping(target = "duration", source = "updateTrainingRequest.duration")
    @Mapping(target = "trainingName", source = "updateTrainingRequest.trainingName")
    @Mapping(target = "trainer", source = "trainer")
    @Mapping(target = "trainee", source = "trainee")
    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(target = "id", ignore = true)
    Training toEntity(UpdateTrainingRequest updateTrainingRequest, Trainer trainer, Trainee trainee, TrainingType trainingType);

}
