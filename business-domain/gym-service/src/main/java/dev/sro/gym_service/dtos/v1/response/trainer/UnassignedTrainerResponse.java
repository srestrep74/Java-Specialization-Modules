package dev.sro.gym_service.dtos.v1.response.trainer;

public record UnassignedTrainerResponse(
    String username,
    String firstName,
    String lastName,
    Long specialization
) {}
