package com.sro.SpringCoreTask1.mappers.trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.entity.Trainee;

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
