package com.sro.SpringCoreTask1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.entity.Training;

@Mapper(
    componentModel = "spring",
    uses = {TraineeMapper.class, TrainerMapper.class, TrainingTypeMapper.class}
)
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(source = "traineeId", target = "trainee.id")
    @Mapping(source = "trainerId", target = "trainer.id")
    @Mapping(source = "trainingTypeId", target = "trainingType.id")
    Training toEntity(TrainingRequestDTO trainingRequestDTO);

    TrainingResponseDTO toDTO(Training training);

}
