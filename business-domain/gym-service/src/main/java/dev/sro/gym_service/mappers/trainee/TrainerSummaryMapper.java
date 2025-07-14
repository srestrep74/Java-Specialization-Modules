package dev.sro.gym_service.mappers.trainee;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.response.trainee.TrainerSummaryResponse;
import dev.sro.gym_service.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TrainerSummaryMapper {
    
    @Mapping(source = "trainingType.id", target = "specialization")
    TrainerSummaryResponse toTrainerSummary(Trainer trainer);

    List<TrainerSummaryResponse> toTrainerSummaryList(List<Trainer> trainers);

}
