package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sro.SpringCoreTask1.service.impl.RedisTokenStorageServiceImpl;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class RedisTokenStorageServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private RedisTokenStorageServiceImpl tokenStorageService;

    private String testUsername = "testuser";
    private String tokenId = "test-token-id";
    private String blacklistPrefix = "bl_";
    private String refreshPrefix = "rt_";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenStorageService, "blacklistKeyPrefix", blacklistPrefix);
        ReflectionTestUtils.setField(tokenStorageService, "refreshTokensKeyPrefix", refreshPrefix);
        ReflectionTestUtils.setField(tokenStorageService, "refreshTokenExpiryDays", 7);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    void blacklistToken_ShouldAddToBlacklist_WhenValidTokenAndExpiry() {
        Instant expiry = Instant.now().plusSeconds(3600);
        long millisToExpiry = expiry.toEpochMilli() - Instant.now().toEpochMilli();

        tokenStorageService.blacklistToken(tokenId, expiry);

        verify(valueOperations).set(eq(blacklistPrefix + tokenId), eq("1"), eq(millisToExpiry),
                eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void blacklistToken_ShouldNotAddToBlacklist_WhenTokenIsNull() {
        tokenStorageService.blacklistToken(null, Instant.now().plusSeconds(3600));

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void blacklistToken_ShouldNotAddToBlacklist_WhenTokenIsEmpty() {
        tokenStorageService.blacklistToken("", Instant.now().plusSeconds(3600));

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void blacklistToken_ShouldNotAddToBlacklist_WhenExpiryIsInPast() {
        Instant expiry = Instant.now().minusSeconds(3600);

        tokenStorageService.blacklistToken(tokenId, expiry);

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void isTokenBlacklisted_ShouldReturnTrue_WhenTokenBlacklisted() {
        when(redisTemplate.hasKey(blacklistPrefix + tokenId)).thenReturn(true);

        boolean result = tokenStorageService.isTokenBlacklisted(tokenId);

        assertTrue(result);
        verify(redisTemplate).hasKey(blacklistPrefix + tokenId);
    }

    @Test
    void isTokenBlacklisted_ShouldReturnFalse_WhenTokenNotBlacklisted() {
        when(redisTemplate.hasKey(blacklistPrefix + tokenId)).thenReturn(false);

        boolean result = tokenStorageService.isTokenBlacklisted(tokenId);

        assertFalse(result);
        verify(redisTemplate).hasKey(blacklistPrefix + tokenId);
    }

    @Test
    void isTokenBlacklisted_ShouldReturnFalse_WhenTokenIsNull() {
        boolean result = tokenStorageService.isTokenBlacklisted(null);

        assertFalse(result);
        verify(redisTemplate, never()).hasKey(anyString());
    }

    @Test
    void storeRefreshToken_ShouldAddToken_WhenValidInput() {
        when(redisTemplate.getExpire(refreshPrefix + testUsername)).thenReturn(-1L);

        tokenStorageService.storeRefreshToken(testUsername, tokenId);

        verify(setOperations).add(refreshPrefix + testUsername, tokenId);
        verify(redisTemplate).expire(refreshPrefix + testUsername, 7, TimeUnit.DAYS);
    }

    @Test
    void storeRefreshToken_ShouldAddTokenWithoutSettingExpiry_WhenKeyAlreadyHasExpiry() {
        when(redisTemplate.getExpire(refreshPrefix + testUsername)).thenReturn(100L);

        tokenStorageService.storeRefreshToken(testUsername, tokenId);

        verify(setOperations).add(refreshPrefix + testUsername, tokenId);
        verify(redisTemplate, never()).expire(anyString(), anyLong(), any());
    }

    @Test
    void storeRefreshToken_ShouldNotAddToken_WhenUsernameIsNull() {
        tokenStorageService.storeRefreshToken(null, tokenId);

        verify(setOperations, never()).add(anyString(), anyString());
    }

    @Test
    void storeRefreshToken_ShouldNotAddToken_WhenTokenIdIsNull() {
        tokenStorageService.storeRefreshToken(testUsername, null);

        verify(setOperations, never()).add(anyString(), anyString());
    }

    @Test
    void getUserRefreshTokens_ShouldReturnTokens_WhenTokensExist() {
        Set<String> mockTokens = new HashSet<>();
        mockTokens.add(tokenId);
        when(setOperations.members(refreshPrefix + testUsername)).thenReturn(mockTokens);

        Set<String> result = tokenStorageService.getUserRefreshTokens(testUsername);

        assertEquals(mockTokens, result);
        verify(setOperations).members(refreshPrefix + testUsername);
    }

    @Test
    void getUserRefreshTokens_ShouldReturnEmptySet_WhenNoTokensExist() {
        when(setOperations.members(refreshPrefix + testUsername)).thenReturn(null);

        Set<String> result = tokenStorageService.getUserRefreshTokens(testUsername);

        assertEquals(Collections.emptySet(), result);
        verify(setOperations).members(refreshPrefix + testUsername);
    }

    @Test
    void getUserRefreshTokens_ShouldReturnEmptySet_WhenUsernameIsNull() {
        Set<String> result = tokenStorageService.getUserRefreshTokens(null);

        assertEquals(Collections.emptySet(), result);
        verify(setOperations, never()).members(anyString());
    }

    @Test
    void removeRefreshToken_ShouldRemoveToken_WhenExists() {
        tokenStorageService.removeRefreshToken(testUsername, tokenId);

        verify(setOperations).remove(refreshPrefix + testUsername, tokenId);
    }

    @Test
    void removeRefreshToken_ShouldDoNothing_WhenUsernameIsNull() {
        tokenStorageService.removeRefreshToken(null, tokenId);

        verify(setOperations, never()).remove(anyString(), anyString());
    }

    @Test
    void removeRefreshToken_ShouldDoNothing_WhenTokenIdIsNull() {
        tokenStorageService.removeRefreshToken(testUsername, null);

        verify(setOperations, never()).remove(anyString(), anyString());
    }

    @Test
    void clearUserRefreshTokens_ShouldDeleteKey() {
        tokenStorageService.clearUserRefreshTokens(testUsername);

        verify(redisTemplate).delete(refreshPrefix + testUsername);
    }

    @Test
    void clearUserRefreshTokens_ShouldDoNothing_WhenUsernameIsNull() {
        tokenStorageService.clearUserRefreshTokens(null);

        verify(redisTemplate, never()).delete(anyString());
    }
}