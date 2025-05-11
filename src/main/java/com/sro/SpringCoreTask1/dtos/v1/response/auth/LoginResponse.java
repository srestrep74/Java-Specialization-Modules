package com.sro.SpringCoreTask1.dtos.v1.response.auth;

public record LoginResponse(
    String username,
    boolean success,
    String token,
    String refreshToken
) {
    // Secondary constructor for backward compatibility
    public LoginResponse(String username, boolean success) {
        this(username, success, null, null);
    }
    
    // Constructor without refresh token for backward compatibility
    public LoginResponse(String username, boolean success, String token) {
        this(username, success, token, null);
    }
}
