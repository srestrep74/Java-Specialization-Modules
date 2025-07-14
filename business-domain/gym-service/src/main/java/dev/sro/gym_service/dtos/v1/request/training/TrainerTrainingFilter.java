package dev.sro.gym_service.dtos.v1.request.training;

import java.time.LocalDate;

public record TrainerTrainingFilter(
    String username,
    LocalDate fromDate,
    LocalDate toDate,
    String traineeName
) {}
