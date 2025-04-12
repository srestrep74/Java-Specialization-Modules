package com.sro.SpringCoreTask1.dtos.v1.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterTrainerRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull Long specialization
) {}
