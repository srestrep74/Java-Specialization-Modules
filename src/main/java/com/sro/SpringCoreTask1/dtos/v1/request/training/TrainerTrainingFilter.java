package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;

public record TrainerTrainingFilter(
    String username,
    LocalDate fromDate,
    LocalDate toDate,
    String traineeName
) {}
