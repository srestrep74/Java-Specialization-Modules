package com.sro.SpringCoreTask1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    TrainingType toEntity(TrainingTypeRequestDTO trainingTypeResponseDTO);
    TrainingTypeResponseDTO toDTO(TrainingType trainingType);
}
