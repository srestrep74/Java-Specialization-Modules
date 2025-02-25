package com.sro.SpringCoreTask1.mappers;

import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.models.Trainee;

public class TraineeMapper {

    public static TraineeDTO toDTO(Trainee trainee) {
        return new TraineeDTO(
            trainee.getUserId(),
            trainee.getFirstName(),
            trainee.getLastName(),
            trainee.getUserName(),
            trainee.getPassword(),
            trainee.isActive(),
            trainee.getAddress(),
            trainee.getDateOfBirth()
        );
    }

    public static Trainee toEntity(TraineeDTO dto) {
        Trainee trainee = new Trainee();
        trainee.setUserId(dto.getUserId());
        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setUserName(dto.getUserName());
        trainee.setActive(dto.isActive());
        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        return trainee;
    }
}
