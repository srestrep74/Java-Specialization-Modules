package com.sro.SpringCoreTask1.dtos.v1.request.trainee;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTraineeProfileRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    LocalDate dateOfBirth,
    String address,
    @NotNull boolean active
) {}
