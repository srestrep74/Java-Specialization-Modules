package dev.sro.gym_service.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.training.TraineeTrainingResponse;
import dev.sro.gym_service.entity.Training;

@Mapper(componentModel = "spring")
public interface TrainingTraineeMapper {
    
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    @Mapping(source = "duration", target = "trainingDuration")
    @Mapping(source = "trainer.username", target = "trainerName")
    TraineeTrainingResponse toTraineeTrainingResponse(Training training);

}
