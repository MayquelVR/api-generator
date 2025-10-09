package com.viewdatatools.apigenarator.auth.adapter.in.web.mapper;

import com.viewdatatools.apigenarator.auth.domain.model.*;
import com.viewdatatools.apigenarator.auth.dto.*;
import org.springframework.stereotype.Component;

@Component
public class AuthDtoMapper {

    public UserRegistration toUserRegistration(RegisterRequest request) {
        return UserRegistration.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public VerificationToken toVerificationToken(VerifyRequest request) {
        return VerificationToken.builder()
                .token(request.getToken())
                .build();
    }

    public LoginCredentials toLoginCredentials(LoginRequest request) {
        return LoginCredentials.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    public LoginResponse toLoginResponse(AuthenticatedUser authenticatedUser) {
        return LoginResponse.builder()
                .username(authenticatedUser.getUsername())
                .email(authenticatedUser.getEmail())
                .token(authenticatedUser.getToken())
                .refreshToken(authenticatedUser.getRefreshToken())
                .expiresIn(authenticatedUser.getExpiresIn())
                .build();
    }

    public ForgotPassword toForgotPassword(ForgotPasswordRequest request) {
        return ForgotPassword.builder()
                .email(request.getEmail())
                .build();
    }

    public PasswordReset toPasswordReset(PasswordResetRequest request) {
        return PasswordReset.builder()
                .token(request.getToken())
                .newPassword(request.getPassword())
                .build();
    }
}
