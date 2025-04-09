package com.sro.SpringCoreTask1.mappers.trainee;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;

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
