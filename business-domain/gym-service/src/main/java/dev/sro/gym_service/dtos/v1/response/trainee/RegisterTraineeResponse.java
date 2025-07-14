package dev.sro.gym_service.dtos.v1.response.trainee;

public record RegisterTraineeResponse(
    String username,
    String password,
    String plainPassword
) {}
