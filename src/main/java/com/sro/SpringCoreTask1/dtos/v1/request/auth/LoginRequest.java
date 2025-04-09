package com.sro.SpringCoreTask1.dtos.v1.request.auth;

public record LoginRequest(
    String username,
    String password
) {}
