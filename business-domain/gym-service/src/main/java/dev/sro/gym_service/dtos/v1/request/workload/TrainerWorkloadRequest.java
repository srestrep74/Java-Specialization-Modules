package dev.sro.gym_service.dtos.v1.request.workload;

import java.time.LocalDate;

public record TrainerWorkloadRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean isActive,
        LocalDate trainingDate,
        int trainingDuration,
        ActionType actionType
) {
    public enum ActionType {
        ADD, DELETE, UPDATE
    }
} 