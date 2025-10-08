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
import org.springframework.stereotype.Service;

@Service
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;

    public LoginUserService(UserRepositoryPort userRepositoryPort,
                            PasswordEncoderPort passwordEncoderPort,
                            JwtServicePort jwtServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
    }

    @Override
    public AuthenticatedUser login(LoginCredentials credentials) {
        UserDomain user = userRepositoryPort.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoderPort.matches(credentials.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtServicePort.generateToken(user.getUsername());

        return AuthenticatedUser.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }
}
