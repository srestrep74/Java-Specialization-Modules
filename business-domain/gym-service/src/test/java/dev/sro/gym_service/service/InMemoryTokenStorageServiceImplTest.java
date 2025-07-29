package dev.sro.gym_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.sro.gym_service.config.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class InMemoryTokenStorageServiceImplTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private InMemoryTokenStorageServiceImpl tokenStorageService;

    private String testUsername = "testuser";
    private String tokenId = "test-token-id";
    private String blacklistPrefix = "bl_";
    private String refreshPrefix = "rt_";

    @BeforeEach
    void setUp() {
        lenient().when(jwtProperties.blacklist()).thenReturn(
                new JwtProperties.BlacklistProperties(blacklistPrefix, "300000"));
        lenient().when(jwtProperties.refresh()).thenReturn(
                new JwtProperties.RefreshProperties(refreshPrefix, 7));
    }

    @Test
    void blacklistToken_ShouldAddToBlacklist_WhenValidTokenAndExpiry() {
        Instant expiry = Instant.now().plusSeconds(3600);

        tokenStorageService.blacklistToken(tokenId, expiry);

        assertTrue(tokenStorageService.isTokenBlacklisted(tokenId));
    }

    @Test
    void blacklistToken_ShouldNotAddToBlacklist_WhenTokenIsNull() {
        tokenStorageService.blacklistToken(null, Instant.now().plusSeconds(3600));

        assertFalse(tokenStorageService.isTokenBlacklisted(null));
    }

    @Test
    void blacklistToken_ShouldNotAddToBlacklist_WhenTokenIsEmpty() {
        tokenStorageService.blacklistToken("", Instant.now().plusSeconds(3600));

        assertFalse(tokenStorageService.isTokenBlacklisted(""));
    }

    @Test
    void blacklistToken_ShouldNotAddToBlacklist_WhenExpiryIsInPast() {
        Instant expiry = Instant.now().minusSeconds(3600);

        tokenStorageService.blacklistToken(tokenId, expiry);

        assertFalse(tokenStorageService.isTokenBlacklisted(tokenId));
    }

    @Test
    void isTokenBlacklisted_ShouldReturnFalse_WhenTokenNotBlacklisted() {
        assertFalse(tokenStorageService.isTokenBlacklisted("non-existent-token"));
    }

    @Test
    void isTokenBlacklisted_ShouldReturnFalse_WhenTokenExpired() {
        Instant pastExpiry = Instant.now().minusSeconds(10);
        Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
        blacklistedTokens.put(blacklistPrefix + tokenId, pastExpiry);

        try {
            java.lang.reflect.Field field = InMemoryTokenStorageServiceImpl.class.getDeclaredField("blacklistedTokens");
            field.setAccessible(true);
            field.set(tokenStorageService, blacklistedTokens);
        } catch (Exception e) {
            fail("Failed to set blacklistedTokens field for testing");
        }

        assertFalse(tokenStorageService.isTokenBlacklisted(tokenId));

        try {
            java.lang.reflect.Field field = InMemoryTokenStorageServiceImpl.class.getDeclaredField("blacklistedTokens");
            field.setAccessible(true);
            Map<String, Instant> updatedBlacklist = (Map<String, Instant>) field.get(tokenStorageService);
            assertFalse(updatedBlacklist.containsKey(blacklistPrefix + tokenId));
        } catch (Exception e) {
            fail("Failed to get blacklistedTokens field for verification");
        }
    }

    @Test
    void storeRefreshToken_ShouldAddToken_WhenValidInput() {
        tokenStorageService.storeRefreshToken(testUsername, tokenId);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(testUsername);
        assertTrue(tokens.contains(tokenId));
    }

    @Test
    void storeRefreshToken_ShouldNotAddToken_WhenUsernameIsNull() {
        tokenStorageService.storeRefreshToken(null, tokenId);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(null);
        assertEquals(Collections.emptySet(), tokens);
    }

    @Test
    void storeRefreshToken_ShouldNotAddToken_WhenTokenIdIsNull() {
        tokenStorageService.storeRefreshToken(testUsername, null);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(testUsername);
        assertEquals(Collections.emptySet(), tokens);
    }

    @Test
    void getUserRefreshTokens_ShouldReturnEmptySet_WhenNoTokensStored() {
        Set<String> tokens = tokenStorageService.getUserRefreshTokens("non-existent-user");

        assertEquals(Collections.emptySet(), tokens);
    }

    @Test
    void getUserRefreshTokens_ShouldReturnEmptySet_WhenUsernameIsNull() {
        Set<String> tokens = tokenStorageService.getUserRefreshTokens(null);

        assertEquals(Collections.emptySet(), tokens);
    }

    @Test
    void getUserRefreshTokens_ShouldReturnCopyOfTokens_NotDirectReference() {
        tokenStorageService.storeRefreshToken(testUsername, tokenId);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(testUsername);
        tokens.add("another-token");

        Set<String> tokensAfterModification = tokenStorageService.getUserRefreshTokens(testUsername);
        assertEquals(1, tokensAfterModification.size());
        assertTrue(tokensAfterModification.contains(tokenId));
    }

    @Test
    void removeRefreshToken_ShouldRemoveToken_WhenExists() {
        tokenStorageService.storeRefreshToken(testUsername, tokenId);
        tokenStorageService.storeRefreshToken(testUsername, "other-token");

        tokenStorageService.removeRefreshToken(testUsername, tokenId);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(testUsername);
        assertFalse(tokens.contains(tokenId));
        assertEquals(1, tokens.size());
    }

    @Test
    void removeRefreshToken_ShouldDoNothing_WhenTokenDoesNotExist() {
        tokenStorageService.storeRefreshToken(testUsername, "other-token");

        tokenStorageService.removeRefreshToken(testUsername, tokenId);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(testUsername);
        assertEquals(1, tokens.size());
    }

    @Test
    void removeRefreshToken_ShouldDoNothing_WhenUserDoesNotExist() {
        tokenStorageService.removeRefreshToken("non-existent-user", tokenId);

        assertDoesNotThrow(() -> tokenStorageService.removeRefreshToken("non-existent-user", tokenId));
    }

    @Test
    void clearUserRefreshTokens_ShouldRemoveAllTokens() {
        tokenStorageService.storeRefreshToken(testUsername, tokenId);
        tokenStorageService.storeRefreshToken(testUsername, "other-token");

        tokenStorageService.clearUserRefreshTokens(testUsername);

        Set<String> tokens = tokenStorageService.getUserRefreshTokens(testUsername);
        assertEquals(Collections.emptySet(), tokens);
    }

    @Test
    void clearUserRefreshTokens_ShouldDoNothing_WhenUserDoesNotExist() {
        assertDoesNotThrow(() -> tokenStorageService.clearUserRefreshTokens("non-existent-user"));
    }

    @Test
    void cleanupExpiredTokens_ShouldRemoveExpiredTokens() {
        Instant futureExpiry = Instant.now().plusSeconds(3600);
        Instant pastExpiry = Instant.now().minusSeconds(3600);

        Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
        blacklistedTokens.put(blacklistPrefix + "valid-token", futureExpiry);
        blacklistedTokens.put(blacklistPrefix + "expired-token", pastExpiry);

        try {
            java.lang.reflect.Field field = InMemoryTokenStorageServiceImpl.class.getDeclaredField("blacklistedTokens");
            field.setAccessible(true);
            field.set(tokenStorageService, blacklistedTokens);
        } catch (Exception e) {
            fail("Failed to set blacklistedTokens field for testing");
        }

        tokenStorageService.cleanupExpiredTokens();

        try {
            java.lang.reflect.Field field = InMemoryTokenStorageServiceImpl.class.getDeclaredField("blacklistedTokens");
            field.setAccessible(true);
            Map<String, Instant> updatedBlacklist = (Map<String, Instant>) field.get(tokenStorageService);
            assertTrue(updatedBlacklist.containsKey(blacklistPrefix + "valid-token"));
            assertFalse(updatedBlacklist.containsKey(blacklistPrefix + "expired-token"));
        } catch (Exception e) {
            fail("Failed to get blacklistedTokens field for verification");
        }
    }
}