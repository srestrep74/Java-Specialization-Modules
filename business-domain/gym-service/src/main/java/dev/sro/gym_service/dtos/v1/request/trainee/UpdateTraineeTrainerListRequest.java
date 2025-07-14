package dev.sro.gym_service.dtos.v1.request.trainee;

import java.util.List;

public record UpdateTraineeTrainerListRequest(
    List<String> trainers
) {}
