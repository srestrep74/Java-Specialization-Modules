package dev.sro.gym_service.dtos.v1.response.trainee;

public record TrainerSummaryResponse(
    String username,
    String firstName,
    String lastName,
    Long specialization
) {}
