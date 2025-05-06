package com.sro.SpringCoreTask1.dtos.v1.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {} 