package com.sro.SpringCoreTask1.dtos.v1.response.auth;

public record LoginResponse(
    String username,
    boolean success
) {}
