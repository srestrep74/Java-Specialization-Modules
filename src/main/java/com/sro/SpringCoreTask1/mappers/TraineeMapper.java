package com.sro.SpringCoreTask1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "id", ignore = true)
    Trainee toEntity(TraineeRequestDTO traineeRequestDTO);
    
    TraineeResponseDTO toDTO(Trainee trainee);
}
