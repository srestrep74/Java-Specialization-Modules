package com.sro.SpringCoreTask1.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainingSeedRequest;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;

@Mapper(
    componentModel = "spring",
    uses = { TraineeSeedMapper.class, TrainerSeedMapper.class, TrainingTypeSeedMapper.class }
)
public interface TrainingSeedMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "trainee", target = "trainee")
    @Mapping(source = "trainer", target = "trainer")
    @Mapping(source = "trainingType", target = "trainingType")
    Training toEntity(TrainingSeedRequest trainingSeedRequest, Trainee trainee, Trainer trainer, TrainingType trainingType);
}
