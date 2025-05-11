package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;

import com.sro.SpringCoreTask1.service.impl.auth.LoginAttemptService;
import com.sro.SpringCoreTask1.util.LoginAttemptInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Spy
    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loginAttemptService, "maxAttempts", 3);
        ReflectionTestUtils.setField(loginAttemptService, "lockTimeMinutes", 5);
    }

    @Test
    void registerFailedAttempt_ShouldIncrementCounter_WhenFirstAttempt() {
        int attempts = loginAttemptService.registerFailedAttempt(testUsername);

        assertEquals(1, attempts);
        assertEquals(1, loginAttemptService.getAttempts(testUsername));
    }

    @Test
    void registerFailedAttempt_ShouldIncrementCounter_WhenMultipleAttempts() {
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);
        int attempts = loginAttemptService.registerFailedAttempt(testUsername);

        assertEquals(3, attempts);
        assertEquals(3, loginAttemptService.getAttempts(testUsername));
    }

    @Test
    void getAttempts_ShouldReturnZero_WhenNoAttempts() {
        int attempts = loginAttemptService.getAttempts("nonexistentuser");

        assertEquals(0, attempts);
    }

    @Test
    void isBlocked_ShouldReturnFalse_WhenAttemptsLessThanMax() {
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);

        boolean blocked = loginAttemptService.isBlocked(testUsername);

        assertFalse(blocked);
    }

    @Test
    void isBlocked_ShouldReturnTrue_WhenAttemptsEqualOrMoreThanMax() {
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);

        boolean blocked = loginAttemptService.isBlocked(testUsername);

        assertTrue(blocked);
    }

    @Test
    void resetAttempts_ShouldRemoveEntry() {
        loginAttemptService.registerFailedAttempt(testUsername);

        loginAttemptService.resetAttempts(testUsername);

        assertEquals(0, loginAttemptService.getAttempts(testUsername));
    }

    @Test
    void getBlockDuration_ShouldReturnConfiguredValue() {
        int duration = loginAttemptService.getBlockDuration();

        assertEquals(5, duration);
    }

    @Test
    void getMaxAttempts_ShouldReturnConfiguredValue() {
        int maxAttempts = loginAttemptService.getMaxAttempts();

        assertEquals(3, maxAttempts);
    }

    @Test
    void getRemainingAttempts_ShouldReturnCorrectValue() {
        loginAttemptService.registerFailedAttempt(testUsername);

        int remaining = loginAttemptService.getRemainingAttempts(testUsername);

        assertEquals(2, remaining);
    }

    @Test
    void getRemainingAttempts_ShouldReturnZero_WhenNoAttemptsLeft() {
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);
        loginAttemptService.registerFailedAttempt(testUsername);

        int remaining = loginAttemptService.getRemainingAttempts(testUsername);

        assertEquals(0, remaining);
    }

    @Test
    void cleanExpiredRecords_ShouldRemoveExpiredEntries() {
        Map<String, LoginAttemptInfo> attemptsCache = new ConcurrentHashMap<>();
        attemptsCache.put("expireduser", new LoginAttemptInfo(2, LocalDateTime.now().minusMinutes(6)));
        attemptsCache.put(testUsername, new LoginAttemptInfo(1, LocalDateTime.now()));

        ReflectionTestUtils.setField(loginAttemptService, "attemptsCache", attemptsCache);

        loginAttemptService.getAttempts("anyuser");

        assertEquals(0, loginAttemptService.getAttempts("expireduser"));
        assertEquals(1, loginAttemptService.getAttempts(testUsername));
    }
}