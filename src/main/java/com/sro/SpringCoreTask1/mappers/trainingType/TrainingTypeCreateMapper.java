package com.sro.SpringCoreTask1.mappers.trainingType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeCreateMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    TrainingType toEntity(TrainingTypeRequestDTO trainingTypeRequestDTO);

}
