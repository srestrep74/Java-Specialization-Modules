package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.entity.RoleType;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.AuthenticationFailedException;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.security.CustomUserDetails;
import com.sro.SpringCoreTask1.service.impl.auth.AuthServiceImpl;
import com.sro.SpringCoreTask1.service.impl.auth.CustomUserDetailsService;
import com.sro.SpringCoreTask1.service.impl.auth.LoginAttemptService;
import com.sro.SpringCoreTask1.util.jwt.JwtUtil;
import com.sro.SpringCoreTask1.util.jwt.TokenBlacklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private CustomUserDetailsService userDetailsService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private LoginAttemptService loginAttemptService;
    
    @Mock
    private TokenBlacklist tokenBlacklist;
    
    @Mock
    private TokenStorageService tokenStorageService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Trainee trainee;
    private Trainer trainer;
    private CustomUserDetails traineeUserDetails;
    private CustomUserDetails trainerUserDetails;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("trainee1");
        trainee.setPassword("encodedPassword1");
        trainee.setActive(true);
        trainee.setRole(RoleType.TRAINEE);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("trainer1");
        trainer.setPassword("encodedPassword1");
        trainer.setActive(true);
        trainer.setRole(RoleType.TRAINER);
        
        traineeUserDetails = new CustomUserDetails(trainee);
        trainerUserDetails = new CustomUserDetails(trainer);
        
        accessToken = "access.token.jwt";
        refreshToken = "refresh.token.jwt";
    }

    @Test
    void authenticate_ShouldReturnLoginResponseForTrainee_WhenCredentialsAreValid() {
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainee1", "password1"))
            .thenReturn(traineeUserDetails);
        when(jwtUtil.generateToken(traineeUserDetails)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(traineeUserDetails)).thenReturn(refreshToken);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("token123");

        LoginResponse response = authService.authenticate("trainee1", "password1");

        assertTrue(response.success());
        assertEquals("trainee1", response.username());
        assertEquals(accessToken, response.token());
        assertEquals(refreshToken, response.refreshToken());
        assertTrue(authService.isAuthenticated());
        assertTrue(authService.isCurrentUserTrainee());
        assertFalse(authService.isCurrentUserTrainer());
        assertEquals(trainee, authService.getCurrentUser());
        
        verify(tokenStorageService).storeRefreshToken("trainee1", "token123");
        verify(loginAttemptService).resetAttempts("trainee1");
    }

    @Test
    void authenticate_ShouldReturnLoginResponseForTrainer_WhenCredentialsAreValid() {
        when(loginAttemptService.isBlocked("trainer1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainer1", "password1"))
            .thenReturn(trainerUserDetails);
        when(jwtUtil.generateToken(trainerUserDetails)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(trainerUserDetails)).thenReturn(refreshToken);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("token123");

        LoginResponse response = authService.authenticate("trainer1", "password1");

        assertTrue(response.success());
        assertEquals("trainer1", response.username());
        assertEquals(accessToken, response.token());
        assertEquals(refreshToken, response.refreshToken());
        assertTrue(authService.isAuthenticated());
        assertFalse(authService.isCurrentUserTrainee());
        assertTrue(authService.isCurrentUserTrainer());
        assertEquals(trainer, authService.getCurrentUser());
        
        verify(tokenStorageService).storeRefreshToken("trainer1", "token123");
        verify(loginAttemptService).resetAttempts("trainer1");
    }

    @Test
    void authenticate_ShouldThrowIllegalArgumentException_WhenCredentialsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate(null, null));
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate("username", null));
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate(null, "password"));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenAccountIsLocked() {
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(true);
        when(loginAttemptService.getBlockDuration()).thenReturn(5);

        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, 
            () -> authService.authenticate("trainee1", "password1"));
            
        assertTrue(exception.getMessage().contains("Account is locked"));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenUserNotFound() {
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainee1", "password1"))
            .thenThrow(new UsernameNotFoundException("User not found"));
        when(loginAttemptService.getRemainingAttempts("trainee1")).thenReturn(2);

        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, 
            () -> authService.authenticate("trainee1", "password1"));
            
        assertTrue(exception.getMessage().contains("Invalid username or password"));
        assertTrue(exception.getMessage().contains("2 attempts remaining"));
        
        verify(loginAttemptService).registerFailedAttempt("trainee1");
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenNoAttemptsRemaining() {
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainee1", "password1"))
            .thenThrow(new UsernameNotFoundException("User not found"));
        when(loginAttemptService.getRemainingAttempts("trainee1")).thenReturn(0);
        when(loginAttemptService.getBlockDuration()).thenReturn(5);

        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, 
            () -> authService.authenticate("trainee1", "password1"));
            
        assertTrue(exception.getMessage().contains("Account locked for 5 minutes"));
        
        verify(loginAttemptService).registerFailedAttempt("trainee1");
    }

    @Test
    void authenticate_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainee1", "password1"))
            .thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> authService.authenticate("trainee1", "password1"));
    }

    @Test
    void refreshToken_ShouldReturnNewTokens_WhenRefreshTokenIsValid() {
        String oldRefreshToken = "old.refresh.token";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        
        when(jwtUtil.isRefreshToken(oldRefreshToken)).thenReturn(true);
        when(jwtUtil.extractTokenId(oldRefreshToken)).thenReturn("oldTokenId");
        when(tokenBlacklist.isBlacklisted("oldTokenId")).thenReturn(false);
        when(jwtUtil.isTokenExpired(oldRefreshToken)).thenReturn(false);
        when(jwtUtil.extractUsername(oldRefreshToken)).thenReturn("trainee1");
        when(userDetailsService.loadUserByUsername("trainee1")).thenReturn(traineeUserDetails);
        when(jwtUtil.generateToken(traineeUserDetails)).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(traineeUserDetails)).thenReturn(newRefreshToken);
        when(jwtUtil.extractTokenId(newRefreshToken)).thenReturn("newTokenId");
        
        LoginResponse response = authService.refreshToken(oldRefreshToken);
        
        assertTrue(response.success());
        assertEquals("trainee1", response.username());
        assertEquals(newAccessToken, response.token());
        assertEquals(newRefreshToken, response.refreshToken());
        
        verify(tokenStorageService).storeRefreshToken("trainee1", "newTokenId");
    }
    
    @Test
    void refreshToken_ShouldThrowIllegalArgumentException_WhenRefreshTokenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(null));
    }
    
    @Test
    void refreshToken_ShouldThrowAuthenticationFailedException_WhenTokenIsNotRefreshToken() {
        when(jwtUtil.isRefreshToken("not.refresh.token")).thenReturn(false);
        
        assertThrows(AuthenticationFailedException.class, () -> authService.refreshToken("not.refresh.token"));
    }
    
    @Test
    void refreshToken_ShouldThrowAuthenticationFailedException_WhenTokenIsBlacklisted() {
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("tokenId");
        when(tokenBlacklist.isBlacklisted("tokenId")).thenReturn(true);
        
        assertThrows(AuthenticationFailedException.class, () -> authService.refreshToken(refreshToken));
    }
    
    @Test
    void refreshToken_ShouldThrowAuthenticationFailedException_WhenTokenIsExpired() {
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("tokenId");
        when(tokenBlacklist.isBlacklisted("tokenId")).thenReturn(false);
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(true);
        
        assertThrows(AuthenticationFailedException.class, () -> authService.refreshToken(refreshToken));
    }

    @Test
    void invalidateToken_ShouldBlacklistToken() {
        Date expiryDate = new Date();
        when(jwtUtil.extractTokenId("token")).thenReturn("tokenId");
        when(jwtUtil.extractExpiration("token")).thenReturn(expiryDate);
        when(tokenBlacklist.isBlacklisted("tokenId")).thenReturn(false);
        when(jwtUtil.extractUsername("token")).thenReturn("trainee1");
        when(jwtUtil.isRefreshToken("token")).thenReturn(true);
        
        authService.invalidateToken("token");
        
        verify(tokenBlacklist).blacklistToken(eq("tokenId"), any(Instant.class));
        verify(tokenStorageService).removeRefreshToken("trainee1", "tokenId");
    }
    
    @Test
    void invalidateToken_ShouldDoNothing_WhenTokenIsNull() {
        authService.invalidateToken(null);
        
        verifyNoInteractions(tokenBlacklist);
        verifyNoInteractions(tokenStorageService);
    }

    @Test
    void changePassword_ShouldUpdatePasswordForTrainee_WhenCredentialsAreValid() {
        // Setup authenticate method mock
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainee1", "password1"))
            .thenReturn(traineeUserDetails);
        when(jwtUtil.generateToken(traineeUserDetails)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(traineeUserDetails)).thenReturn(refreshToken);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("token123");
        
        // Setup for password matching and encoding
        when(passwordEncoder.matches("password1", "encodedPassword1")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        
        authService.changePassword("trainee1", "password1", "newpassword");
        
        assertEquals("encodedNewPassword", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void changePassword_ShouldUpdatePasswordForTrainer_WhenCredentialsAreValid() {
        // Setup authenticate method mock
        when(loginAttemptService.isBlocked("trainer1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainer1", "password1"))
            .thenReturn(trainerUserDetails);
        when(jwtUtil.generateToken(trainerUserDetails)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(trainerUserDetails)).thenReturn(refreshToken);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("token123");
        
        // Setup for password matching and encoding
        when(passwordEncoder.matches("password1", "encodedPassword1")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        
        authService.changePassword("trainer1", "password1", "newpassword");
        
        assertEquals("encodedNewPassword", trainer.getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void changePassword_ShouldThrowIllegalArgumentException_WhenParametersAreNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword("user", null, null));
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null, "pass", null));
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null, null, "newpass"));
    }

    @Test
    void changePassword_ShouldThrowAuthenticationFailedException_WhenCurrentPasswordIncorrect() {
        // Setup authenticate method mock
        when(loginAttemptService.isBlocked("trainee1")).thenReturn(false);
        when(userDetailsService.loadUserByUsernameAndPassword("trainee1", "password1"))
            .thenReturn(traineeUserDetails);
        when(jwtUtil.generateToken(traineeUserDetails)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(traineeUserDetails)).thenReturn(refreshToken);
        when(jwtUtil.extractTokenId(refreshToken)).thenReturn("token123");
        
        when(passwordEncoder.matches("password1", "encodedPassword1")).thenReturn(false);
        
        assertThrows(AuthenticationFailedException.class, 
            () -> authService.changePassword("trainee1", "password1", "newpassword"));
    }

    @Test
    void logout_ShouldClearCurrentUserAndBlacklistTokens() {
        // Setup for getting user refresh tokens
        authService.setCurrentUser(trainee);
        Set<String> refreshTokenIds = new HashSet<>();
        refreshTokenIds.add("token1");
        refreshTokenIds.add("token2");
        
        when(tokenStorageService.getUserRefreshTokens("trainee1")).thenReturn(refreshTokenIds);
        
        authService.logout();
        
        assertNull(authService.getCurrentUser());
        assertFalse(authService.isAuthenticated());
        
        verify(tokenBlacklist, times(2)).blacklistToken(anyString(), any(Instant.class));
        verify(tokenStorageService).clearUserRefreshTokens("trainee1");
    }

    @Test
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
        authService.setCurrentUser(trainee);
        
        assertEquals(trainee, authService.getCurrentUser());
    }

    @Test
    void isAuthenticated_ShouldReturnFalse_WhenNoUserLoggedIn() {
        assertFalse(authService.isAuthenticated());
    }

    @Test
    void isCurrentUserTrainee_ShouldReturnFalse_WhenNoUserLoggedIn() {
        assertFalse(authService.isCurrentUserTrainee());
    }

    @Test
    void isCurrentUserTrainer_ShouldReturnFalse_WhenNoUserLoggedIn() {
        assertFalse(authService.isCurrentUserTrainer());
    }
}