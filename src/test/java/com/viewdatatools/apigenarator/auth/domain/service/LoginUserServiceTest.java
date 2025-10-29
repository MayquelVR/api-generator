package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.exception.InvalidCredentialsException;
import com.viewdatatools.apigenarator.auth.domain.exception.UserNotFoundException;
import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.model.LoginCredentials;
import com.viewdatatools.apigenarator.auth.domain.model.UserDomain;
import com.viewdatatools.apigenarator.auth.domain.port.out.JwtServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.PasswordEncoderPort;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtServicePort jwtServicePort;

    @InjectMocks
    private LoginUserService loginUserService;

    private UserDomain testUser;
    private LoginCredentials loginCredentials;

    @BeforeEach
    void setUp() {
        testUser = UserDomain.builder()
            .uuid(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .build();

        loginCredentials = LoginCredentials.builder()
            .username("testuser")
            .password("password123")
            .build();
    }

    @Test
    void login_withValidCredentials_shouldReturnAuthenticatedUser() {
        when(userRepositoryPort.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoderPort.matches("password123", "encodedPassword"))
            .thenReturn(true);
        when(jwtServicePort.generateToken("testuser"))
            .thenReturn("accessToken");
        when(jwtServicePort.generateRefreshToken("testuser"))
            .thenReturn("refreshToken");

        AuthenticatedUser result = loginUserService.login(loginCredentials);

        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(jwtServicePort).generateToken("testuser");
        verify(jwtServicePort).generateRefreshToken("testuser");
    }

    @Test
    void login_withInvalidUsername_shouldThrowException() {
        when(userRepositoryPort.findByUsername("testuser"))
            .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> loginUserService.login(loginCredentials)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(jwtServicePort, never()).generateToken(anyString());
    }

    @Test
    void login_withInvalidPassword_shouldThrowException() {
        when(userRepositoryPort.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoderPort.matches("password123", "encodedPassword"))
            .thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> loginUserService.login(loginCredentials)
        );

        assertTrue(exception.getMessage().contains("Invalid credentials"));
        verify(jwtServicePort, never()).generateToken(anyString());
    }

    @Test
    void login_shouldGenerateBothTokens() {
        when(userRepositoryPort.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoderPort.matches(anyString(), anyString()))
            .thenReturn(true);
        when(jwtServicePort.generateToken("testuser"))
            .thenReturn("accessToken");
        when(jwtServicePort.generateRefreshToken("testuser"))
            .thenReturn("refreshToken");

        AuthenticatedUser result = loginUserService.login(loginCredentials);

        assertNotNull(result.getToken());
        assertNotNull(result.getRefreshToken());
        verify(jwtServicePort).generateToken("testuser");
        verify(jwtServicePort).generateRefreshToken("testuser");
    }

    @Test
    void login_shouldVerifyPassword() {
        when(userRepositoryPort.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoderPort.matches("password123", "encodedPassword"))
            .thenReturn(true);
        when(jwtServicePort.generateToken(anyString()))
            .thenReturn("accessToken");
        when(jwtServicePort.generateRefreshToken(anyString()))
            .thenReturn("refreshToken");

        loginUserService.login(loginCredentials);

        verify(passwordEncoderPort).matches("password123", "encodedPassword");
    }
}
