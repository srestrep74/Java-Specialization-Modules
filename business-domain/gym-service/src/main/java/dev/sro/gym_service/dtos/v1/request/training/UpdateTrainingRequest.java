package dev.sro.gym_service.dtos.v1.request.training;

import java.time.LocalDate;

public record UpdateTrainingRequest(
    String trainingName,
    LocalDate trainingDate,
    int duration,
    String trainerUsername,
    String traineeUsername,
    String trainingType
) {}
