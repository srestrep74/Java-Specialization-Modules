package dev.sro.gym_service.mappers.trainer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.response.trainer.RegisterTrainerResponse;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.TrainingType;
import dev.sro.gym_service.mappers.trainingType.TrainingTypeCreateMapper;

@Mapper(componentModel = "spring", uses = {TrainingTypeCreateMapper.class})
public interface TrainerCreateMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(source = "trainingType", target = "trainingType")
    @Mapping(target = "role", ignore = true)
    Trainer toEntity(RegisterTrainerRequest dto, TrainingType trainingType);

    @Mapping(source = "trainer.username", target = "username")
    @Mapping(source = "trainer.password", target = "password")
    @Mapping(source = "plainPassword", target = "plainPassword")
    RegisterTrainerResponse toRegisterResponse(Trainer trainer, String plainPassword);
}
