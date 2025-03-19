package com.sro.SpringCoreTask1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    TrainingType toEntity(TrainingTypeRequestDTO trainingTypeRequestDTO);

    TrainingTypeResponseDTO toDTO(TrainingType trainingType);
}