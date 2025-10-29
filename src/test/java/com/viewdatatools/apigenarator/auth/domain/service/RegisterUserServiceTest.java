package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.exception.EmailAlreadyExistsException;
import com.viewdatatools.apigenarator.auth.domain.exception.UsernameAlreadyExistsException;
import com.viewdatatools.apigenarator.auth.domain.model.UserRegistration;
import com.viewdatatools.apigenarator.auth.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private TokenServicePort tokenServicePort;

    @Mock
    private VerificationTokenPort verificationTokenPort;

    @Mock
    private MailServicePort mailServicePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RegisterUserService registerUserService;

    private UserRegistration testUserRegistration;

    @BeforeEach
    void setUp() {
        testUserRegistration = UserRegistration.builder()
            .uuid(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .build();
    }

    @Test
    void registerUser_withValidData_shouldRegisterSuccessfully() {
        when(userRepositoryPort.existsByUsername("testuser")).thenReturn(false);
        when(userRepositoryPort.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoderPort.encode("password123")).thenReturn("encodedPassword");
        when(tokenServicePort.generateToken()).thenReturn("verification-token");
        when(tokenServicePort.hash(anyString())).thenReturn("hashed-token");

        assertDoesNotThrow(() -> registerUserService.register(testUserRegistration));

        verify(passwordEncoderPort).encode("password123");
        verify(verificationTokenPort).create(
            eq(testUserRegistration.getUuid()),
            eq("testuser"),
            eq("test@example.com"),
            eq("encodedPassword"),
            eq("hashed-token")
        );
        verify(mailServicePort).sendMail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void registerUser_withExistingUsername_shouldThrowException() {
        when(userRepositoryPort.existsByUsername("testuser")).thenReturn(true);

        UsernameAlreadyExistsException exception = assertThrows(
            UsernameAlreadyExistsException.class,
            () -> registerUserService.register(testUserRegistration)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(verificationTokenPort, never()).create(any(), any(), any(), any(), any());
    }

    @Test
    void registerUser_withExistingEmail_shouldThrowException() {
        when(userRepositoryPort.existsByUsername("testuser")).thenReturn(false);
        when(userRepositoryPort.existsByEmail("test@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> registerUserService.register(testUserRegistration)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(verificationTokenPort, never()).create(any(), any(), any(), any(), any());
    }

    @Test
    void registerUser_shouldEncodePassword() {
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode("password123")).thenReturn("encodedPassword");
        when(tokenServicePort.generateToken()).thenReturn("verification-token");
        when(tokenServicePort.hash(anyString())).thenReturn("hashed-token");

        registerUserService.register(testUserRegistration);

        verify(passwordEncoderPort).encode("password123");
    }

    @Test
    void registerUser_shouldGenerateVerificationToken() {
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
        when(tokenServicePort.generateToken()).thenReturn("verification-token");
        when(tokenServicePort.hash(anyString())).thenReturn("hashed-token");

        registerUserService.register(testUserRegistration);

        verify(tokenServicePort).generateToken();
        verify(tokenServicePort).hash("verification-token");
    }

    @Test
    void registerUser_shouldSendVerificationEmail() {
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
        when(tokenServicePort.generateToken()).thenReturn("verification-token");
        when(tokenServicePort.hash(anyString())).thenReturn("hashed-token");

        registerUserService.register(testUserRegistration);

        verify(mailServicePort).sendMail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void registerUser_shouldCreateVerificationTokenWithCorrectData() {
        UUID userUuid = testUserRegistration.getUuid();
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode("password123")).thenReturn("encodedPassword");
        when(tokenServicePort.generateToken()).thenReturn("verification-token");
        when(tokenServicePort.hash("verification-token")).thenReturn("hashed-token");

        registerUserService.register(testUserRegistration);

        verify(verificationTokenPort).create(
            eq(userUuid),
            eq("testuser"),
            eq("test@example.com"),
            eq("encodedPassword"),
            eq("hashed-token")
        );
    }

    @Test
    void registerUser_shouldNotCreateTokenIfUsernameExists() {
        when(userRepositoryPort.existsByUsername("testuser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () ->
            registerUserService.register(testUserRegistration)
        );

        verify(tokenServicePort, never()).generateToken();
        verify(verificationTokenPort, never()).create(any(), any(), any(), any(), any());
    }

    @Test
    void registerUser_shouldNotCreateTokenIfEmailExists() {
        when(userRepositoryPort.existsByUsername("testuser")).thenReturn(false);
        when(userRepositoryPort.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () ->
            registerUserService.register(testUserRegistration)
        );

        verify(tokenServicePort, never()).generateToken();
        verify(verificationTokenPort, never()).create(any(), any(), any(), any(), any());
    }
}
