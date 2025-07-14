package dev.sro.gym_service.dtos.v1.response.auth;

public record LoginResponse(
    String username,
    boolean success,
    String token,
    String refreshToken
) {
    public LoginResponse(String username, boolean success) {
        this(username, success, null, null);
    }
    
    public LoginResponse(String username, boolean success, String token) {
        this(username, success, token, null);
    }
}
