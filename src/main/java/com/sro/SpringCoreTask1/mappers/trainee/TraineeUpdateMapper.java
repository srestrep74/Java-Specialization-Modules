package com.sro.SpringCoreTask1.mappers.trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.TraineeUpdateRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeUpdateResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;

@Mapper(componentModel = "spring", uses = { TrainerSummaryMapper.class })
public interface TraineeUpdateMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainee toEntity(TraineeUpdateRequestDTO dto, @MappingTarget Trainee trainee);

    @Mapping(source = "trainers", target = "trainers")
    TraineeUpdateResponseDTO toUpdateResponseDTO(Trainee trainee);

}
