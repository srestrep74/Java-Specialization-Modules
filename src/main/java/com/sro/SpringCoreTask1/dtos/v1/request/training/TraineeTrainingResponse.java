package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;

public record TraineeTrainingResponse(
    String trainingName,
    LocalDate trainingDate,
    String trainingType,
    int trainingDuration,
    String trainerName
) {}
