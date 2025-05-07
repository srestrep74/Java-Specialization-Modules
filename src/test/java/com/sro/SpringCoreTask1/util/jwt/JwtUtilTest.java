package com.sro.SpringCoreTask1.util.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5Zm9ydGVzdGluZ2p3dHRva2Vuc2FuZGl0c2hvdWxkYmVhdGxlYXN0NjRieXRlc2xvbmc=");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 86400000L); // 1 day

        jwtUtil.init();

        lenient().when(userDetails.getUsername()).thenReturn(username);

        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN"));
        lenient().when(userDetails.getAuthorities()).thenReturn((Collection) authorities);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
        assertFalse(jwtUtil.isTokenExpired(token));

        List<String> roles = jwtUtil.extractRoles(token);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidRefreshToken() {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(refreshToken));
        assertFalse(jwtUtil.isTokenExpired(refreshToken));
        assertTrue(jwtUtil.isRefreshToken(refreshToken));

        List<String> roles = jwtUtil.extractRoles(refreshToken);
        assertTrue(roles.isEmpty());
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtUtil.generateToken(userDetails);

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("otheruser");

        boolean isValid = jwtUtil.validateToken(token, otherUser);

        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() throws Exception {
        long originalExpiration = (long) ReflectionTestUtils.getField(jwtUtil, "expiration");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L);
        String token = jwtUtil.generateToken(userDetails);
        ReflectionTestUtils.setField(jwtUtil, "expiration", originalExpiration);

        Thread.sleep(10);

        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    void extractTokenId_ShouldReturnTokenId() {
        String token = jwtUtil.generateToken(userDetails);

        String tokenId = jwtUtil.extractTokenId(token);

        assertNotNull(tokenId);
        assertFalse(tokenId.isEmpty());
    }

    @Test
    void extractClaim_ShouldExtractCustomClaim() {
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("customClaim", "customValue");
        String token = jwtUtil.generateToken(userDetails, customClaims);

        String customClaim = jwtUtil.extractClaim(token, c -> c.get("customClaim", String.class));

        assertEquals("customValue", customClaim);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsMalformed() {
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.thisIsNotAValidToken";

        boolean isValid = jwtUtil.validateToken(malformedToken, userDetails);

        assertFalse(isValid);
    }

    @Test
    void isRefreshToken_ShouldReturnFalse_ForAccessToken() {
        String accessToken = jwtUtil.generateToken(userDetails);

        boolean isRefreshToken = jwtUtil.isRefreshToken(accessToken);

        assertFalse(isRefreshToken);
    }

    @Test
    void isRefreshToken_ShouldReturnTrue_ForRefreshToken() {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        boolean isRefreshToken = jwtUtil.isRefreshToken(refreshToken);

        assertTrue(isRefreshToken);
    }
}