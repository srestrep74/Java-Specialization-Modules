package com.sro.SpringCoreTask1.dtos.v1.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTrainerProfileRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull Long specialization,
    @NotNull boolean active
) {}
