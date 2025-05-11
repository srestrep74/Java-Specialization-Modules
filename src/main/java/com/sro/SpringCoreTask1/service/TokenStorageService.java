package com.sro.SpringCoreTask1.service;

import java.time.Instant;
import java.util.Set;

public interface TokenStorageService {

    void blacklistToken(String tokenId, Instant expiryDate);

    boolean isTokenBlacklisted(String tokenId);

    void storeRefreshToken(String username, String tokenId);

    Set<String> getUserRefreshTokens(String username);

    void removeRefreshToken(String username, String tokenId);

    void clearUserRefreshTokens(String username);
}