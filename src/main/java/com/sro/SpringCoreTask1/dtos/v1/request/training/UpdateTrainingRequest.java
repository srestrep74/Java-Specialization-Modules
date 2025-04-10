package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;

public record UpdateTrainingRequest(
    String trainingName,
    LocalDate trainingDate,
    int duration,
    String trainerUsername,
    String traineeUsername,
    String trainingType
) {}
