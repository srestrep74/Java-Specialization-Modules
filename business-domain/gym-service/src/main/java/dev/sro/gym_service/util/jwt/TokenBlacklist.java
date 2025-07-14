package dev.sro.gym_service.util.jwt;

import dev.sro.gym_service.service.TokenStorageService;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenBlacklist {

    private final TokenStorageService tokenStorageService;

    public TokenBlacklist(TokenStorageService tokenStorageService) {
        this.tokenStorageService = tokenStorageService;
    }

    public void blacklistToken(String tokenId, Instant expiryDate) {
        tokenStorageService.blacklistToken(tokenId, expiryDate);
    }

    public boolean isBlacklisted(String tokenId) {
        return tokenStorageService.isTokenBlacklisted(tokenId);
    }
}