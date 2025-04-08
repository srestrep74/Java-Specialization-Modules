package com.sro.SpringCoreTask1.mappers.trainee;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TrainerSummaryMapper {
    
    @Mapping(source = "trainingType.id", target = "specialization")
    TrainerSummaryResponseDTO toTrainerSummary(Trainer trainer);

    List<TrainerSummaryResponseDTO> toTrainerSummaryList(List<Trainer> trainers);

}
