package dev.sro.gym_service.dtos.v1.response.training;

import java.time.LocalDate;

public record TrainingSummaryResponse(
    String trainingName,
    String trainerName,
    String traineeName,
    String trainingType,
    LocalDate trainingDate,
    int trainingDuration
) {}
