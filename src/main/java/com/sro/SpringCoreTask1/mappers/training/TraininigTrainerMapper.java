package com.sro.SpringCoreTask1.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.entity.Training;

@Mapper(componentModel = "spring")
public interface TraininigTrainerMapper {
    
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "duration", target = "trainingDuration")
    @Mapping(source = "trainee.username", target = "traineeName")
    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    TrainerTrainingResponse toTrainerTrainingResponse(Training training);

}
