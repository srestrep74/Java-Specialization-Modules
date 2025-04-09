package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;

public record CreateTrainingRequest(
    String traineeUsername,
    String trainerUsername,
    String trainingName,
    LocalDate trainingDate,
    String trainingDuration
) {}
