package dev.sro.gym_service.dtos.v1.request.seed;

import java.time.LocalDate;

public record TrainingSeedRequest(
    String trainingName,
    LocalDate trainingDate,
    int duration,
    Long traineeId,
    Long trainerId,
    Long trainingTypeId
) {}
