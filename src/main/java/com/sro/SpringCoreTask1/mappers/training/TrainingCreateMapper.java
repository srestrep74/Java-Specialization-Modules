package com.sro.SpringCoreTask1.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;

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
