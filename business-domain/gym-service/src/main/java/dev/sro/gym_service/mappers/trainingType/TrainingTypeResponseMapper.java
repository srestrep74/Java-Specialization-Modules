package dev.sro.gym_service.mappers.trainingType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.response.trainingType.TrainingTypeResponse;
import dev.sro.gym_service.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeResponseMapper {
    
    @Mapping(target = "trainingTypeId", source = "trainingType.id")
    @Mapping(target = "trainingTypeName", source = "trainingType.trainingTypeName")
    TrainingTypeResponse mapToResponse(TrainingType trainingType);

}
