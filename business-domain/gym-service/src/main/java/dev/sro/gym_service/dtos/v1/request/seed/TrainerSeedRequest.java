package dev.sro.gym_service.dtos.v1.request.seed;

public record TrainerSeedRequest(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    Long trainingTypeId,
    String role
) {}
