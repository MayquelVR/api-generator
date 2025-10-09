package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.model.LoginCredentials;
import com.viewdatatools.apigenarator.auth.domain.model.UserDomain;
import com.viewdatatools.apigenarator.auth.domain.port.in.LoginUserUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.JwtServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.PasswordEncoderPort;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.auth.domain.exception.InvalidCredentialsException;
import com.viewdatatools.apigenarator.auth.domain.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;
    private final long jwtExpirationTime;

    public LoginUserService(UserRepositoryPort userRepositoryPort,
                            PasswordEncoderPort passwordEncoderPort,
                            JwtServicePort jwtServicePort,
                            @Value("${jwt.expiration:86400000}") long jwtExpirationTime) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
        this.jwtExpirationTime = jwtExpirationTime;
    }

    @Override
    public AuthenticatedUser login(LoginCredentials credentials) {
        UserDomain user = userRepositoryPort.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoderPort.matches(credentials.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtServicePort.generateToken(user.getUsername());
        String refreshToken = jwtServicePort.generateRefreshToken(user.getUsername());

        return AuthenticatedUser.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpirationTime)
                .build();
    }
}
