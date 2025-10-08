package com.viewdatatools.apigenarator.auth.adapter.in.web;

import com.viewdatatools.apigenarator.auth.adapter.in.web.mapper.AuthDtoMapper;
import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.port.in.*;
import com.viewdatatools.apigenarator.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyUserUseCase verifyUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final AuthDtoMapper authDtoMapper;

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        requestPasswordResetUseCase.requestPasswordReset(
                authDtoMapper.toPasswordResetRequest(request)
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.resetPassword(authDtoMapper.toPasswordReset(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
