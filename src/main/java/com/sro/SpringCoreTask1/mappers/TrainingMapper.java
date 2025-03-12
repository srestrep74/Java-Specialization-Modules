package com.sro.SpringCoreTask1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(
    componentModel = "spring",
    uses = {TraineeMapper.class, TrainerMapper.class, TrainingTypeMapper.class}
)
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "trainee", target = "trainee")
    @Mapping(source = "trainer", target = "trainer")
    @Mapping(source = "trainingType", target = "trainingType")
    Training toEntity(TrainingRequestDTO trainingRequestDTO, Trainee trainee, Trainer trainer, TrainingType trainingType);

    TrainingResponseDTO toDTO(Training training);

}
