package com.sro.SpringCoreTask1.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainingTypeSeedRequest;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeSeedMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    TrainingType toEntity(TrainingTypeSeedRequest trainingTypeSeedRequest);
    
}
