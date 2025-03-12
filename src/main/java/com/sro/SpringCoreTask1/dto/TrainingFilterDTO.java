package com.sro.SpringCoreTask1.dto;

import java.time.LocalDate;

public record TrainingFilterDTO(
    Long traineeId,
    LocalDate fromDate,
    LocalDate toDate,
    String trainerName,
    String trainingType
){}
