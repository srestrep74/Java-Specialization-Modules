package com.sro.SpringCoreTask1.mappers.trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.TraineeRegistrationResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;

@Mapper(componentModel = "spring", uses = { TrainerSummaryMapper.class})
public interface TraineeResponseMapper {
    
    TraineeResponseDTO toDTO(Trainee trainee);

    TraineeRegistrationResponse toRegistrationResponseDTO(Trainee trainee);

    @Mapping(target = "trainers", source = "trainers")
    TraineeProfileResponseDTO toProfileResponse(Trainee trainee);

}
