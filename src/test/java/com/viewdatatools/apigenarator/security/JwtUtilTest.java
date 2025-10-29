package com.viewdatatools.apigenarator.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String SECRET = "mySecretKeyForTestingPurposesThisIsAVeryLongSecretKey123456789";
    private static final long EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    void generateToken_shouldGenerateValidToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateRefreshToken_shouldGenerateValidRefreshToken() {
        String token = jwtUtil.generateRefreshToken(TEST_USERNAME, TEST_EMAIL);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldExtractCorrectUsername() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        String username = jwtUtil.extractUsername(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void getUsernameFromToken_shouldExtractCorrectUsername() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        String username = jwtUtil.getUsernameFromToken(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void getEmailFromToken_shouldExtractCorrectEmail() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        String email = jwtUtil.getEmailFromToken(token);

        assertEquals(TEST_EMAIL, email);
    }

    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        boolean isValid = jwtUtil.validateToken(token, TEST_USERNAME);

        assertTrue(isValid);
    }

    @Test
    void validateToken_withWrongUsername_shouldReturnFalse() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        boolean isValid = jwtUtil.validateToken(token, "wronguser");

        assertFalse(isValid);
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        boolean isValid = jwtUtil.validateToken("invalid.token.here", TEST_USERNAME);

        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_withFreshToken_shouldReturnFalse() {
        String token = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_withExpiredToken_shouldReturnTrue() {
        // Create JWT with immediate expiration
        JwtUtil shortLivedJwtUtil = new JwtUtil(SECRET, 1); // 1ms
        String token = shortLivedJwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        try {
            Thread.sleep(10); // Wait for token to expire
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isExpired = shortLivedJwtUtil.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    void canTokenBeRefreshed_withRefreshToken_shouldReturnTrue() {
        String refreshToken = jwtUtil.generateRefreshToken(TEST_USERNAME, TEST_EMAIL);

        boolean canRefresh = jwtUtil.canTokenBeRefreshed(refreshToken);

        assertTrue(canRefresh);
    }

    @Test
    void canTokenBeRefreshed_withAccessToken_shouldReturnFalse() {
        String accessToken = jwtUtil.generateToken(TEST_USERNAME, TEST_EMAIL);

        boolean canRefresh = jwtUtil.canTokenBeRefreshed(accessToken);

        assertFalse(canRefresh);
    }

    @Test
    void canTokenBeRefreshed_withInvalidToken_shouldReturnFalse() {
        boolean canRefresh = jwtUtil.canTokenBeRefreshed("invalid.token.here");

        assertFalse(canRefresh);
    }

    @Test
    void generateToken_shouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtUtil.generateToken("user1", "user1@example.com");
        String token2 = jwtUtil.generateToken("user2", "user2@example.com");

        assertNotEquals(token1, token2);
    }
}

