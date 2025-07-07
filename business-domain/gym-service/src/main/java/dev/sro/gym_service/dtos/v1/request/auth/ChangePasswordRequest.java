package dev.sro.gym_service.dtos.v1.request.auth;

public record ChangePasswordRequest(
    String username,
    String oldPassword,
    String newPassword
) {}
