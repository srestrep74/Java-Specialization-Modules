package dev.sro.workload_service.dtos.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import dev.sro.workload_service.entity.enums.ActionType;

public record TrainerWorkloadRequest(
        @NotBlank(message = "Trainer username is required")
        String trainerUsername,

        @NotBlank(message = "Trainer first name is required")
        String trainerFirstName,

        @NotBlank(message = "Trainer last name is required")
        String trainerLastName,

        @NotNull(message = "isActive status is required")
        Boolean isActive,

        @NotNull(message = "Training date is required")
        LocalDate trainingDate,

        @NotNull(message = "Training duration is required")
        Integer trainingDuration,

        @NotNull(message = "Action type is required")
        ActionType actionType
) {
} 