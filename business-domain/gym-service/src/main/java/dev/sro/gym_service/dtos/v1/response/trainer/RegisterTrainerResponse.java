package dev.sro.gym_service.dtos.v1.response.trainer;

public record RegisterTrainerResponse(
    String username,
    String password,
    String plainPassword
) {}
