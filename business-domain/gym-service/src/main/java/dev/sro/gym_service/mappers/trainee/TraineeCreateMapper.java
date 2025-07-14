package dev.sro.gym_service.mappers.trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.entity.Trainee;

@Mapper(componentModel = "spring")
public interface TraineeCreateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "role", ignore = true)
    Trainee toEntity(RegisterTraineeRequest dto);

    @Mapping(source = "trainee.username", target = "username")
    @Mapping(source = "trainee.password", target = "password")
    @Mapping(source = "plainPassword", target = "plainPassword")
    RegisterTraineeResponse toRegisterResponse(Trainee trainee, String plainPassword);
}
