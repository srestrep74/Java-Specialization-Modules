package dev.sro.gym_service.mappers.trainee;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.sro.gym_service.dtos.v1.response.trainee.TraineeProfileResponse;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TraineeResponseMapper {
    
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainers", target = "trainers")
    TraineeProfileResponse toProfileResponse(Trainee trainee);

    List<TraineeProfileResponse.TrainerInfo> toTrainerInfoList(List<Trainer> trainers);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "trainingType.id", target = "specialization")
    TraineeProfileResponse.TrainerInfo toTrainerInfo(Trainer trainer);

}
