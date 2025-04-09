package com.sro.SpringCoreTask1.mappers.trainer;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.mappers.TrainingTypeMapper;

@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class})
public interface TrainerUpdateMapper {
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dto.firstName", target = "firstName")
    @Mapping(source = "dto.lastName", target = "lastName")
    @Mapping(source = "trainingType", target = "trainingType")
    @Mapping(source = "dto.active", target = "active")
    @Mapping(source = "dto.username", target = "username")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "password", ignore = true)
    Trainer toEntity(UpdateTrainerProfileRequest dto, @MappingTarget Trainer existingTrainer, TrainingType trainingType);

}
