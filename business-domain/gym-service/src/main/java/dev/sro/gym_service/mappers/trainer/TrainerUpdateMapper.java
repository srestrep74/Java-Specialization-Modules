package dev.sro.gym_service.mappers.trainer;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.TrainingType;
import dev.sro.gym_service.mappers.trainingType.TrainingTypeCreateMapper;

@Mapper(componentModel = "spring", uses = {TrainingTypeCreateMapper.class})
public interface TrainerUpdateMapper {
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dto.firstName", target = "firstName")
    @Mapping(source = "dto.lastName", target = "lastName")
    @Mapping(source = "trainingType", target = "trainingType")
    @Mapping(source = "dto.active", target = "active")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    Trainer toEntity(UpdateTrainerProfileRequest dto, @MappingTarget Trainer existingTrainer, TrainingType trainingType);

}
