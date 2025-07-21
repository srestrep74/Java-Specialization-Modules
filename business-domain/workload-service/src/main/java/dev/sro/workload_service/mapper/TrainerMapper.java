package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.Trainer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "trainingSessions", ignore = true)
    @Mapping(target = "monthlySummaries", ignore = true)
    @Mapping(target = "username", source = "trainerUsername")
    @Mapping(target = "firstName", source = "trainerFirstName")
    @Mapping(target = "lastName", source = "trainerLastName")
    @Mapping(target = "isActive", source = "isActive")
    Trainer toTrainer(TrainerWorkloadRequest request);
} 