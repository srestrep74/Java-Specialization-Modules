package dev.sro.gym_service.mappers.trainer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.response.trainee.TrainerSummaryResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.TrainerProfileResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.UnassignedTrainerResponse;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TrainerResponseMapper {
    
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "trainingType.id", target = "specialization")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainees", target = "trainees")
    TrainerProfileResponse toTrainerProfileResponse(Trainer trainer);

    List<TrainerProfileResponse.TraineeInfo> toTraineeInfoList(List<Trainee> trainees);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    TrainerProfileResponse.TraineeInfo toTraineeInfo(Trainee trainee);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "trainingType.id", target = "specialization")
    UnassignedTrainerResponse toUnassignedTrainerResponse(Trainer trainer);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "trainingType.id", target = "specialization")
    TrainerSummaryResponse toSummaryResponseDTO(Trainer trainer);
}
