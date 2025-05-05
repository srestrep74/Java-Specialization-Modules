package com.sro.SpringCoreTask1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.util.LoginAttemptInfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    @Value("${security.login.max-attempts:3}")
    private int maxAttempts;

    @Value("${security.login.lock-time-minutes:5}")
    private int lockTimeMinutes;

    private final Map<String, LoginAttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public int registerFailedAttempt(String username) {
        cleanExpiredRecords();

        LoginAttemptInfo info = attemptsCache.computeIfAbsent(
                username,
                k -> new LoginAttemptInfo(0, LocalDateTime.now()));

        LoginAttemptInfo updatedInfo = new LoginAttemptInfo(
                info.getAttempts() + 1,
                LocalDateTime.now());

        attemptsCache.put(username, updatedInfo);

        int attempts = updatedInfo.getAttempts();

        return attempts;
    }

    public int getAttempts(String username) {
        cleanExpiredRecords();

        LoginAttemptInfo info = attemptsCache.get(username);
        return info != null ? info.getAttempts() : 0;
    }

    public boolean isBlocked(String username) {
        cleanExpiredRecords();

        LoginAttemptInfo info = attemptsCache.get(username);
        return info != null && info.getAttempts() >= maxAttempts;
    }

    public void resetAttempts(String username) {
        if (attemptsCache.containsKey(username)) {
            attemptsCache.remove(username);
        }
    }

    public int getBlockDuration() {
        return lockTimeMinutes;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getRemainingAttempts(String username) {
        int attempts = getAttempts(username);
        int remaining = maxAttempts - attempts;
        return Math.max(remaining, 0);
    }

    private void cleanExpiredRecords() {
        LocalDateTime now = LocalDateTime.now();
        attemptsCache.entrySet().removeIf(entry -> {
            LocalDateTime blockedTime = entry.getValue().getTimestamp();
            return blockedTime.plusMinutes(lockTimeMinutes).isBefore(now);
        });
    }
}