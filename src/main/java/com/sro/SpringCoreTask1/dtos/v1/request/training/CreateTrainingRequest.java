package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTrainingRequest(
    @NotBlank String traineeUsername,
    @NotBlank String trainerUsername,
    @NotBlank String trainingName,
    @NotNull LocalDate trainingDate,
    @Min(value = 1) int trainingDuration
) {}
