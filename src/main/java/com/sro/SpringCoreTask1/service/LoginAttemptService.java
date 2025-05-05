package com.sro.SpringCoreTask1.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_TIME_MINUTES = 5;

    private final Map<String, LoginAttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public Integer registerFailedAttempt(String username) {
        cleanExpiredRecords();

        LoginAttemptInfo info = attemptsCache.get(username);
        if (info == null) {
            info = new LoginAttemptInfo(1, LocalDateTime.now());
        } else {
            info = new LoginAttemptInfo(info.getAttempts() + 1, LocalDateTime.now());
        }
        attemptsCache.put(username, info);
        return info.getAttempts();
    }

    public Integer getAttempts(String username) {
        cleanExpiredRecords();

        LoginAttemptInfo info = attemptsCache.get(username);
        return info != null ? info.getAttempts() : null;
    }

    public boolean isBlocked(String username) {
        cleanExpiredRecords();

        LoginAttemptInfo info = attemptsCache.get(username);
        return info != null && info.getAttempts() >= MAX_ATTEMPTS;
    }

    public void resetAttempts(String username) {
        attemptsCache.remove(username);
    }

    public long getBlockDuration() {
        return LOCK_TIME_MINUTES;
    }

    public int getRemainingAttempts(String username) {
        Integer attempts = getAttempts(username);
        if (attempts == null) {
            return MAX_ATTEMPTS;
        }
        int remaining = MAX_ATTEMPTS - attempts;
        return remaining > 0 ? remaining : 0;
    }

    private void cleanExpiredRecords() {
        LocalDateTime now = LocalDateTime.now();
        attemptsCache.entrySet().removeIf(entry -> {
            LocalDateTime blockedTime = entry.getValue().getTimestamp();
            return blockedTime.plusMinutes(LOCK_TIME_MINUTES).isBefore(now);
        });
    }

    private static class LoginAttemptInfo {
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
}
