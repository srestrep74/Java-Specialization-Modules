package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.Trainer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "trainingSessions", ignore = true)
    @Mapping(target = "monthlySummaries", ignore = true)
    @Mapping(target = "username", source = "trainerUsername")
    @Mapping(target = "firstName", source = "trainerFirstName")
    @Mapping(target = "lastName", source = "trainerLastName")
    @Mapping(target = "isActive", source = "isActive")
    Trainer toTrainer(TrainerWorkloadRequest request);
} 