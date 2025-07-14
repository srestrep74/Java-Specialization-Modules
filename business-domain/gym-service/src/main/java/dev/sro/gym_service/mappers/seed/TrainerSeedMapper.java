package dev.sro.gym_service.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.seed.TrainerSeedRequest;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.TrainingType;

@Mapper(componentModel = "spring", uses = TrainingTypeSeedMapper.class)
public interface TrainerSeedMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(source = "trainerSeedRequest.role", target = "role")
    Trainer toEntity(TrainerSeedRequest trainerSeedRequest, TrainingType trainingType);
    
}
