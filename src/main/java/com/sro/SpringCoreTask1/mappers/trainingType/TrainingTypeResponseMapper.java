package com.sro.SpringCoreTask1.mappers.trainingType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeResponseMapper {
    
    @Mapping(target = "trainingTypeId", source = "trainingType.id")
    @Mapping(target = "trainingTypeName", source = "trainingType.trainingTypeName")
    TrainingTypeResponse mapToResponse(TrainingType trainingType);

}
