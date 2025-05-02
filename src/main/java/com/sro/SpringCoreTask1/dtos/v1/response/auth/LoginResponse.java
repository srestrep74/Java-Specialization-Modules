package com.sro.SpringCoreTask1.dtos.v1.response.auth;

public record LoginResponse(
    String username,
    boolean success,
    String token
) {
    // Secondary constructor for backward compatibility
    public LoginResponse(String username, boolean success) {
        this(username, success, null);
    }
}
