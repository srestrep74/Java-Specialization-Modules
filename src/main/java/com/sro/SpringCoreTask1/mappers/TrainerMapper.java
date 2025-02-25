package com.sro.SpringCoreTask1.mappers;

import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.models.Trainer;

public class TrainerMapper {

    public static TrainerDTO toDTO(Trainer trainer) {
        return new TrainerDTO(
            trainer.getUserId(),
            trainer.getFirstName(),
            trainer.getLastName(),
            trainer.getUserName(),
            trainer.getPassword(),
            trainer.isActive(),
            trainer.getSpecialization(),
            trainer.getTrainingType()
        );
    }

    public static Trainer toEntity(TrainerDTO dto) {
        Trainer trainer = new Trainer();
        trainer.setUserId(dto.getUserId());
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainer.setUserName(dto.getUserName());
        trainer.setActive(dto.isActive());
        trainer.setSpecialization(dto.getSpecialization());
        trainer.setTrainingType(dto.getTrainingType());
        return trainer;
    }
}
