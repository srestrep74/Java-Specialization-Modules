package com.sro.SpringCoreTask1.service.impl.auth;

import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.User;
import com.sro.SpringCoreTask1.exception.AuthenticationFailedException;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.security.CustomUserDetails;
import com.sro.SpringCoreTask1.service.AuthService;
import com.sro.SpringCoreTask1.service.LoginAttemptService;
import com.sro.SpringCoreTask1.service.TokenStorageService;
import com.sro.SpringCoreTask1.util.jwt.JwtUtil;
import com.sro.SpringCoreTask1.util.jwt.TokenBlacklist;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklist tokenBlacklist;
    private final TokenStorageService tokenStorageService;

    private User authenticatedUser;
    private boolean isTrainee;

    public AuthServiceImpl(
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService,
            TokenBlacklist tokenBlacklist,
            TokenStorageService tokenStorageService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.tokenBlacklist = tokenBlacklist;
        this.tokenStorageService = tokenStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be null or empty");
        }

        if (loginAttemptService.isBlocked(username)) {
            int remainingMinutes = loginAttemptService.getBlockDuration();
            throw new AuthenticationFailedException("Account is locked. Try again in " + remainingMinutes + " minutes");
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndPassword(username, password);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            String refreshTokenId = jwtUtil.extractTokenId(refreshToken);
            tokenStorageService.storeRefreshToken(username, refreshTokenId);

            if (userDetails instanceof CustomUserDetails) {
                this.authenticatedUser = ((CustomUserDetails) userDetails).getUser();
                this.isTrainee = ((CustomUserDetails) userDetails).getRole().equals("TRAINEE");
            }

            loginAttemptService.resetAttempts(username);

            return new LoginResponse(username, true, accessToken, refreshToken);
        } catch (UsernameNotFoundException e) {
            loginAttemptService.registerFailedAttempt(username);
            int remainingAttempts = loginAttemptService.getRemainingAttempts(username);
            String errorMsg = "Invalid username or password";
            if (remainingAttempts <= 0) {
                errorMsg += String.format(". Account locked for %d minutes", loginAttemptService.getBlockDuration());
            } else {
                errorMsg += String.format(". %d attempts remaining", remainingAttempts);
            }
            throw new AuthenticationFailedException(errorMsg);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error during authentication", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        try {
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new AuthenticationFailedException("Invalid refresh token");
            }

            String tokenId = jwtUtil.extractTokenId(refreshToken);
            if (tokenBlacklist.isBlacklisted(tokenId)) {
                throw new AuthenticationFailedException("Refresh token has been revoked");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                throw new AuthenticationFailedException("Refresh token has expired");
            }

            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            invalidateToken(refreshToken);

            String newRefreshTokenId = jwtUtil.extractTokenId(newRefreshToken);
            tokenStorageService.storeRefreshToken(username, newRefreshTokenId);

            if (this.authenticatedUser == null && userDetails instanceof CustomUserDetails) {
                this.authenticatedUser = ((CustomUserDetails) userDetails).getUser();
                this.isTrainee = ((CustomUserDetails) userDetails).getRole().equals("TRAINEE");
            }

            return new LoginResponse(username, true, newAccessToken, newRefreshToken);

        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error refreshing token", e);
        }
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        try {
            String tokenId = jwtUtil.extractTokenId(token);
            if (tokenId == null || tokenId.isEmpty()) {
                return;
            }

            Date expiryDate = jwtUtil.extractExpiration(token);
            if (expiryDate == null) {
                return;
            }

            if (tokenBlacklist.isBlacklisted(tokenId)) {
                return;
            }

            tokenBlacklist.blacklistToken(tokenId, expiryDate.toInstant());

            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.isRefreshToken(token)) {
                tokenStorageService.removeRefreshToken(username, tokenId);
            }

        } catch (ExpiredJwtException e) {
            String tokenId = e.getClaims().getId();
            String username = e.getClaims().getSubject();

            if (tokenId != null && !tokenId.isEmpty()) {
                tokenBlacklist.blacklistToken(tokenId, Instant.now().plusSeconds(60));

                if (!e.getClaims().containsKey("roles")) { // It's a refresh token
                    tokenStorageService.removeRefreshToken(username, tokenId);
                }
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Error invalidating token", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || username.isEmpty() ||
                oldPassword == null || oldPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Username, old password and new password cannot be null or empty");
        }

        try {
            LoginResponse loginResponse = authenticate(username, oldPassword);

            if (!loginResponse.success()) {
                throw new AuthenticationFailedException("Current password is incorrect");
            }

            if (isCurrentUserTrainee()) {
                Trainee trainee = (Trainee) authenticatedUser;
                if (!passwordEncoder.matches(oldPassword, trainee.getPassword())) {
                    throw new AuthenticationFailedException("Current password is incorrect");
                }
                trainee.setPassword(passwordEncoder.encode(newPassword));
                traineeRepository.save(trainee);
            } else {
                Trainer trainer = (Trainer) authenticatedUser;
                if (!passwordEncoder.matches(oldPassword, trainer.getPassword())) {
                    throw new AuthenticationFailedException("Current password is incorrect");
                }
                trainer.setPassword(passwordEncoder.encode(newPassword));
                trainerRepository.save(trainer);
            }
        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error changing password", e);
        }
    }

    @Override
    public void logout() {
        if (this.authenticatedUser != null) {
            String username = this.authenticatedUser.getUsername();
            Set<String> refreshTokenIds = tokenStorageService.getUserRefreshTokens(username);

            if (refreshTokenIds != null) {
                for (String tokenId : refreshTokenIds) {
                    tokenBlacklist.blacklistToken(tokenId, Instant.now().plus(Duration.ofDays(7)));
                }

                tokenStorageService.clearUserRefreshTokens(username);
            }
        }

        this.authenticatedUser = null;
    }

    @Override
    public User getCurrentUser() {
        return this.authenticatedUser;
    }

    @Override
    public void setCurrentUser(User user) {
        this.authenticatedUser = user;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticatedUser != null;
    }

    @Override
    public boolean isCurrentUserTrainee() {
        return isAuthenticated() && this.isTrainee;
    }

    @Override
    public boolean isCurrentUserTrainer() {
        return isAuthenticated() && !this.isTrainee;
    }
}