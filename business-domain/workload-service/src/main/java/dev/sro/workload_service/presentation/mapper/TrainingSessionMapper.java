package dev.sro.workload_service.presentation.mapper;

import dev.sro.workload_service.domain.entity.TrainingSession;
import dev.sro.workload_service.presentation.dto.request.TrainerWorkloadRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TrainingSessionMapper {

    TrainingSessionMapper INSTANCE = Mappers.getMapper(TrainingSessionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TrainingSession toTrainingSession(TrainerWorkloadRequest request);
} 