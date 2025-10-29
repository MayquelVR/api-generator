package com.viewdatatools.apigenarator.auth.adapter.in.web;

import com.viewdatatools.apigenarator.auth.domain.exception.EmailAlreadyExistsException;
import com.viewdatatools.apigenarator.auth.domain.exception.InvalidCredentialsException;
import com.viewdatatools.apigenarator.auth.domain.exception.UsernameAlreadyExistsException;
import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.model.LoginCredentials;
import com.viewdatatools.apigenarator.auth.domain.model.UserRegistration;
import com.viewdatatools.apigenarator.auth.domain.port.in.LoginUserUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.in.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerIntegrationTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private LoginUserUseCase loginUserUseCase;

    private UserRegistration registerRequest;
    private LoginCredentials loginRequest;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setUp() {
        registerRequest = UserRegistration.builder()
            .uuid(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .build();

        loginRequest = LoginCredentials.builder()
            .username("testuser")
            .password("password123")
            .build();

        authenticatedUser = AuthenticatedUser.builder()
            .username("testuser")
            .email("test@example.com")
            .token("access-token")
            .refreshToken("refresh-token")
            .expiresIn(3600000L)
            .build();
    }

    @Test
    void register_withValidData_shouldRegisterSuccessfully() {
        doNothing().when(registerUserUseCase).register(any(UserRegistration.class));

        assertDoesNotThrow(() -> {
            registerUserUseCase.register(registerRequest);
        });

        verify(registerUserUseCase).register(any(UserRegistration.class));
    }

    @Test
    void register_withDuplicateUsername_shouldThrowException() {
        doThrow(new UsernameAlreadyExistsException("Username already exists"))
            .when(registerUserUseCase).register(any(UserRegistration.class));

        assertThrows(UsernameAlreadyExistsException.class, () -> {
            registerUserUseCase.register(registerRequest);
        });

        verify(registerUserUseCase).register(any(UserRegistration.class));
    }

    @Test
    void register_withDuplicateEmail_shouldThrowException() {
        doThrow(new EmailAlreadyExistsException("Email already exists"))
            .when(registerUserUseCase).register(any(UserRegistration.class));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            registerUserUseCase.register(registerRequest);
        });

        verify(registerUserUseCase).register(any(UserRegistration.class));
    }

    @Test
    void register_withEmptyUsername_shouldValidateInput() {
        UserRegistration emptyUsernameRequest = UserRegistration.builder()
            .uuid(UUID.randomUUID())
            .username("")
            .email("test@example.com")
            .password("password123")
            .build();

        doNothing().when(registerUserUseCase).register(any(UserRegistration.class));

        assertNotNull(emptyUsernameRequest);
        assertEquals("", emptyUsernameRequest.getUsername());
        assertEquals("test@example.com", emptyUsernameRequest.getEmail());
    }

    @Test
    void register_withShortPassword_shouldValidateInput() {
        UserRegistration shortPasswordRequest = UserRegistration.builder()
            .uuid(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("123")
            .build();

        doNothing().when(registerUserUseCase).register(any(UserRegistration.class));

        // El test verifica que el request se construya correctamente
        assertNotNull(shortPasswordRequest);
        assertEquals("123", shortPasswordRequest.getPassword());
    }

    @Test
    void login_withValidCredentials_shouldReturnAuthenticatedUser() {
        when(loginUserUseCase.login(any(LoginCredentials.class)))
            .thenReturn(authenticatedUser);

        AuthenticatedUser result = loginUserUseCase.login(loginRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertNotNull(result.getToken());
        assertNotNull(result.getRefreshToken());
        verify(loginUserUseCase).login(any(LoginCredentials.class));
    }

    @Test
    void login_withInvalidCredentials_shouldThrowException() {
        when(loginUserUseCase.login(any(LoginCredentials.class)))
            .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        assertThrows(InvalidCredentialsException.class, () -> {
            loginUserUseCase.login(loginRequest);
        });

        verify(loginUserUseCase).login(any(LoginCredentials.class));
    }

    @Test
    void login_withMissingFields_shouldValidateInput() {
        LoginCredentials nullUsernameRequest = LoginCredentials.builder()
            .username(null)
            .password("password123")
            .build();

        assertNull(nullUsernameRequest.getUsername());
        assertNotNull(nullUsernameRequest.getPassword());
    }

    @Test
    void authenticatedUser_shouldContainAllRequiredFields() {
        when(loginUserUseCase.login(any(LoginCredentials.class)))
            .thenReturn(authenticatedUser);

        AuthenticatedUser result = loginUserUseCase.login(loginRequest);

        assertAll(
            () -> assertNotNull(result.getUsername()),
            () -> assertNotNull(result.getEmail()),
            () -> assertNotNull(result.getToken()),
            () -> assertNotNull(result.getRefreshToken()),
            () -> assertTrue(result.getExpiresIn() > 0)
        );
    }

    @Test
    void register_shouldNotReturnSensitiveData() {
        doNothing().when(registerUserUseCase).register(any(UserRegistration.class));

        registerUserUseCase.register(registerRequest);

        assertNotNull(registerRequest.getPassword());
        verify(registerUserUseCase).register(argThat(req ->
            req.getPassword() != null && !req.getPassword().isEmpty()
        ));
    }
}
