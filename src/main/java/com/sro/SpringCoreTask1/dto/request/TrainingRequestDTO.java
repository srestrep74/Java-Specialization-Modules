package com.sro.SpringCoreTask1.dto.request;

import java.time.LocalDate;

public record TrainingRequestDTO(
    String trainingName,
    LocalDate trainingDate,
    int duration,
    Long traineeId,
    Long trainerId,
    Long trainingTypeId
){}
