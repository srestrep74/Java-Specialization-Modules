package dev.sro.gym_service.mappers.seed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.request.seed.TrainingSeedRequest;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.entity.TrainingType;

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
