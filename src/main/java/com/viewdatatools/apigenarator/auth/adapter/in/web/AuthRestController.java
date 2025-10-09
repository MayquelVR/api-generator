package com.viewdatatools.apigenarator.auth.adapter.in.web;

import com.viewdatatools.apigenarator.auth.adapter.in.web.mapper.AuthDtoMapper;
import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.port.in.*;
import com.viewdatatools.apigenarator.auth.dto.*;
import com.viewdatatools.apigenarator.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyUserUseCase verifyUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final AuthDtoMapper authDtoMapper;
    private final JwtUtil jwtUtil;
    private final long jwtExpirationTime;

    public AuthRestController(
            RegisterUserUseCase registerUserUseCase,
            VerifyUserUseCase verifyUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase,
            ResetPasswordUseCase resetPasswordUseCase,
            AuthDtoMapper authDtoMapper,
            JwtUtil jwtUtil,
            @Value("${jwt.expiration:3600000}") long jwtExpirationTime
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.verifyUserUseCase = verifyUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.authDtoMapper = authDtoMapper;
        this.jwtUtil = jwtUtil;
        this.jwtExpirationTime = jwtExpirationTime;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.register(authDtoMapper.toUserRegistration(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyRequest request) {
        verifyUserUseCase.verify(authDtoMapper.toVerificationToken(request));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedUser authenticatedUser = loginUserUseCase.login(
                authDtoMapper.toLoginCredentials(request)
        );

        LoginResponse response = authDtoMapper.toLoginResponse(authenticatedUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validar que sea un refresh token
            if (!jwtUtil.canTokenBeRefreshed(refreshToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid refresh token"));
            }

            // Extraer información del refresh token
            String username = jwtUtil.extractUsername(refreshToken);
            String email = jwtUtil.getEmailFromToken(refreshToken);

            // Validar que no esté expirado
            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Refresh token expired. Please log in again."));
            }

            // Generar nuevos tokens
            String newAccessToken = jwtUtil.generateToken(username, email);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, email);

            LoginResponse response = LoginResponse.builder()
                    .username(username)
                    .email(email)
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(jwtExpirationTime) // Usar configuración dinámica
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid or expired refresh token"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.forgotPassword(
                authDtoMapper.toForgotPassword(request)
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        resetPasswordUseCase.resetPassword(authDtoMapper.toPasswordReset(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // Clase interna para respuestas de error
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
