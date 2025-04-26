package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;


public record TraineeTrainingFilter(
    String username,
    LocalDate fromDate,
    LocalDate toDate,
    String trainerName,
    String trainingType
) {}
