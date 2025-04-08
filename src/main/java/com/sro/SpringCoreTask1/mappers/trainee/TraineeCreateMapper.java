package com.sro.SpringCoreTask1.mappers.trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.TraineeRegistrationRequest;
import com.sro.SpringCoreTask1.entity.Trainee;

@Mapper(componentModel = "spring")
public interface TraineeCreateMapper {
    
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "id", ignore = true)
    Trainee toEntity(TraineeRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    Trainee toEntity(TraineeRegistrationRequest dto);
}
