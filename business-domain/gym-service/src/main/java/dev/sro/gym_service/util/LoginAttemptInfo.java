package dev.sro.gym_service.util;

import java.time.LocalDateTime;

public class LoginAttemptInfo {
    private final int attempts;
    private final LocalDateTime timestamp;

    public LoginAttemptInfo(int attempts, LocalDateTime timestamp) {
        this.attempts = attempts;
        this.timestamp = timestamp;
    }

    public int getAttempts() {
        return attempts;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}