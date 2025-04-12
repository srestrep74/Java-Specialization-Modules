package com.sro.SpringCoreTask1.dtos.v1.request.training;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record TraineeTrainingFilter(
    @NotBlank @Size(max = 50) String username,
    @PastOrPresent LocalDate fromDate,
    LocalDate toDate,
    @Size(max = 50) String trainerName,
    @Size(max = 50) String trainingType
) {}
