package com.sro.SpringCoreTask1.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainerSeedRequest;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(componentModel = "spring", uses = TrainingTypeSeedMapper.class)
public interface TrainerSeedMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(source = "trainerSeedRequest.role", target = "role")
    Trainer toEntity(TrainerSeedRequest trainerSeedRequest, TrainingType trainingType);
    
}
