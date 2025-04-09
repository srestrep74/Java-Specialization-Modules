package com.sro.SpringCoreTask1.mappers.trainer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;

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
}
