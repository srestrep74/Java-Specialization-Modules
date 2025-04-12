package com.sro.SpringCoreTask1.mappers.training;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.training.UpdateTrainingRequest;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;

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
