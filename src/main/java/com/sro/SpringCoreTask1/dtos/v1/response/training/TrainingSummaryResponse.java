package com.sro.SpringCoreTask1.dtos.v1.response.training;

import java.time.LocalDate;

public record TrainingSummaryResponse(
    String trainingName,
    String trainerName,
    String traineeName,
    String trainingType,
    LocalDate trainingDate,
    int trainingDuration
) {}
