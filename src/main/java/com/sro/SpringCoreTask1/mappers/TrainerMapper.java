package com.sro.SpringCoreTask1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(source = "trainingTypeId", target = "trainingType.id")
    Trainer toEntity(TrainerRequestDTO trainerRequestDTO);

    TrainerResponseDTO toDTO(Trainer trainer);
}
