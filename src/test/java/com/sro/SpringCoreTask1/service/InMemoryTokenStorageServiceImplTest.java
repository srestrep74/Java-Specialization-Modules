package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sro.SpringCoreTask1.service.impl.InMemoryTokenStorageServiceImpl;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class InMemoryTokenStorageServiceImplTest {

    @Spy
    @InjectMocks
    private InMemoryTokenStorageServiceImpl tokenStorageService;

    private String testUsername = "testuser";
    private String tokenId = "test-token-id";
    private String blacklistPrefix = "bl_";
    private String refreshPrefix = "rt_";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenStorageService, "blacklistKeyPrefix", blacklistPrefix);
        ReflectionTestUtils.setField(tokenStorageService, "refreshTokensKeyPrefix", refreshPrefix);
        ReflectionTestUtils.setField(tokenStorageService, "refreshTokenExpiryDays", 7);
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
        ReflectionTestUtils.setField(tokenStorageService, "blacklistedTokens", blacklistedTokens);

        assertFalse(tokenStorageService.isTokenBlacklisted(tokenId));

        Map<String, Instant> updatedBlacklist = (Map<String, Instant>) ReflectionTestUtils.getField(tokenStorageService,
                "blacklistedTokens");
        assertFalse(updatedBlacklist.containsKey(blacklistPrefix + tokenId));
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
        ReflectionTestUtils.setField(tokenStorageService, "blacklistedTokens", blacklistedTokens);

        tokenStorageService.cleanupExpiredTokens();

        Map<String, Instant> updatedBlacklist = (Map<String, Instant>) ReflectionTestUtils.getField(tokenStorageService,
                "blacklistedTokens");
        assertTrue(updatedBlacklist.containsKey(blacklistPrefix + "valid-token"));
        assertFalse(updatedBlacklist.containsKey(blacklistPrefix + "expired-token"));
    }
}