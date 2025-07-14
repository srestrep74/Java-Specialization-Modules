package dev.sro.gym_service.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.seed.TrainingTypeSeedRequest;
import dev.sro.gym_service.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeSeedMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    TrainingType toEntity(TrainingTypeSeedRequest trainingTypeSeedRequest);
    
}
