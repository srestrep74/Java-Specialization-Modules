package com.sro.SpringCoreTask1.dto.response;

import java.time.LocalDate;

public record TrainingResponseDTO(
    Long id,
    String trainingName,
    LocalDate trainingDate,
    int duration,
    TraineeResponseDTO trainee,
    TrainerResponseDTO trainer,
    TrainingTypeResponseDTO trainingType
){}
