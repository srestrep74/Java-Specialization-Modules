package com.sro.SpringCoreTask1.dto;

import java.time.LocalDate;

public record TrainerTrainingFilterDTO(
    Long trainerId,
    LocalDate fromDate,
    LocalDate toDate,
    String traineeName,
    String trainingType
){}
