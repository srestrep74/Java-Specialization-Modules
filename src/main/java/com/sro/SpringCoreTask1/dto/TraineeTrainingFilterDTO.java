package com.sro.SpringCoreTask1.dto;

import java.time.LocalDate;

public record TraineeTrainingFilterDTO(
    Long traineeId,
    LocalDate fromDate,
    LocalDate toDate,
    String trainerName,
    String trainingType
){}
