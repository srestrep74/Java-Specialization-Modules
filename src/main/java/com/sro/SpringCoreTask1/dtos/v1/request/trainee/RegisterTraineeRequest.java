package com.sro.SpringCoreTask1.dtos.v1.request.trainee;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record RegisterTraineeRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    LocalDate dateOfBirth,
    String address
) {}
