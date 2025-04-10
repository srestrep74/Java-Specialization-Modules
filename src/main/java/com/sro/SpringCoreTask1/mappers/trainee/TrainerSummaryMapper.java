package com.sro.SpringCoreTask1.mappers.trainee;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponse;
import com.sro.SpringCoreTask1.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TrainerSummaryMapper {
    
    @Mapping(source = "trainingType.id", target = "specialization")
    TrainerSummaryResponse toTrainerSummary(Trainer trainer);

    List<TrainerSummaryResponse> toTrainerSummaryList(List<Trainer> trainers);

}
