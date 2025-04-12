package com.sro.SpringCoreTask1.dtos.v1.request.auth;

public record ChangePasswordRequest(
    String username,
    String oldPassword,
    String newPassword
) {}
