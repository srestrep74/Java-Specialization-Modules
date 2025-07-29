package dev.sro.workload_service.util.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.sro.workload_service.config.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtUtil jwtUtil;

    private String secret;
    private Key signingKey;
    private String username;
    private List<String> roles;
    private String tokenId;

    @BeforeEach
    void setUp() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.secret = Base64.getEncoder().encodeToString(key.getEncoded());
        this.signingKey = key;

        lenient().when(jwtProperties.secret()).thenReturn(this.secret);
        jwtUtil.init();

        username = "testUser";
        roles = List.of("USER", "ADMIN");
        tokenId = UUID.randomUUID().toString();
    }

    private String generateToken(String subject, List<String> roles, String tokenId, Date expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setId(tokenId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private String generateExpiredToken() {
        return generateToken(username, roles, tokenId, new Date(System.currentTimeMillis() - 1000));
    }

    private String generateValidToken() {
        return generateToken(username, roles, tokenId, new Date(System.currentTimeMillis() + 60000));
    }

    @Test
    void testExtractUsername() {
        String token = generateValidToken();
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testExtractExpiration() {
        String token = generateValidToken();
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractTokenId() {
        String token = generateValidToken();
        assertEquals(tokenId, jwtUtil.extractTokenId(token));
    }

    @Test
    void testExtractRoles() {
        String token = generateValidToken();
        assertEquals(roles, jwtUtil.extractRoles(token));
    }

    @Test
    void testValidateToken_Valid() {
        String token = generateValidToken();
        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testValidateToken_Expired() {
        String token = generateExpiredToken();
        assertFalse(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.validateToken(token, username));
    }

    @Test
    void testIsTokenExpired_True() {
        String token = generateExpiredToken();
        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testIsTokenExpired_False() {
        String token = generateValidToken();
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testInit_InvalidSecret() {
        Key shortKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String shortSecret = Base64.getEncoder().encodeToString(shortKey.getEncoded());

        when(jwtProperties.secret()).thenReturn(shortSecret);
        
        JwtUtil testJwtUtil = new JwtUtil(jwtProperties);
        assertThrows(IllegalStateException.class, () -> testJwtUtil.init());
    }
}