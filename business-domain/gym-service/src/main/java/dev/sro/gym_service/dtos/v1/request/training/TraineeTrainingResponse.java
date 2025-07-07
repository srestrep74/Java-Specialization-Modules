package dev.sro.gym_service.dtos.v1.request.training;

import java.time.LocalDate;

public record TraineeTrainingResponse(
    String trainingName,
    LocalDate trainingDate,
    String trainingType,
    int trainingDuration,
    String trainerName
) {}
