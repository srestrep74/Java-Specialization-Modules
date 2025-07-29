package dev.sro.gym_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.sro.gym_service.config.properties.LoginProperties;
import dev.sro.gym_service.service.impl.auth.LoginAttemptService;
import dev.sro.gym_service.util.LoginAttemptInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private LoginProperties loginProperties;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        lenient().when(loginProperties.maxAttempts()).thenReturn(3);
        lenient().when(loginProperties.lockTimeMinutes()).thenReturn(5);
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
        verify(loginProperties).lockTimeMinutes();
    }

    @Test
    void getMaxAttempts_ShouldReturnConfiguredValue() {
        int maxAttempts = loginAttemptService.getMaxAttempts();

        assertEquals(3, maxAttempts);
        verify(loginProperties).maxAttempts();
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
        LoginAttemptService testService = new LoginAttemptService(loginProperties);

        try {
            java.lang.reflect.Field attemptsCacheField = LoginAttemptService.class.getDeclaredField("attemptsCache");
            attemptsCacheField.setAccessible(true);
            
            Map<String, LoginAttemptInfo> attemptsCache = new ConcurrentHashMap<>();
            attemptsCache.put("expireduser", new LoginAttemptInfo(2, LocalDateTime.now().minusMinutes(6)));
            attemptsCache.put(testUsername, new LoginAttemptInfo(1, LocalDateTime.now()));
            
            attemptsCacheField.set(testService, attemptsCache);
            
            testService.getAttempts("anyuser");
            
            assertEquals(0, testService.getAttempts("expireduser"));
            assertEquals(1, testService.getAttempts(testUsername));
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }
}