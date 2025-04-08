package com.sro.SpringCoreTask1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.TraineeRegistrationRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.TraineeUpdateRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.TraineeRegistrationResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeUpdateResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "id", ignore = true)
    Trainee toEntity(TraineeRequestDTO traineeRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    Trainee toEntity(TraineeRegistrationRequest registrationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainee toEntity(TraineeUpdateRequestDTO dto);
    
    TraineeResponseDTO toDTO(Trainee trainee);

    TraineeRegistrationResponse toRegistrationResponseDTO(Trainee trainee);

    @Mapping(source =  "trainingType.id", target = "specialization")
    TrainerSummaryResponseDTO toTrainerSummary(Trainer trainer);
    
    List<TrainerSummaryResponseDTO> toTrainerSummaryList(List<Trainer> trainer);

    @Mapping(target = "trainers", source = "trainers")
    TraineeProfileResponseDTO toProfileResponse(Trainee trainee);

    @Mapping(source = "trainers", target = "trainers")
    TraineeUpdateResponseDTO toUpdateResponseDTO(Trainee trainee);
}
