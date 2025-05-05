package com.sro.SpringCoreTask1.util;

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