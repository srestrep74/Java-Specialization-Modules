package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.TrainingSession;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TrainingSession toTrainingSession(TrainerWorkloadRequest request);
} 