package dev.sro.gym_service.mappers.trainingType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import dev.sro.gym_service.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeCreateMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    TrainingType toEntity(TrainingTypeRequestDTO trainingTypeRequestDTO);

}
