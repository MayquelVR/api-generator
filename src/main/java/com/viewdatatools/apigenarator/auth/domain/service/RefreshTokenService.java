package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.port.in.RefreshTokenUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.JwtServicePort;
import com.viewdatatools.apigenarator.auth.domain.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtServicePort jwtServicePort;
    private final long jwtExpirationTime;

    public RefreshTokenService(JwtServicePort jwtServicePort,
                               @Value("${jwt.expiration:86400000}") long jwtExpirationTime) {
        this.jwtServicePort = jwtServicePort;
        this.jwtExpirationTime = jwtExpirationTime;
    }

    @Override
    public AuthenticatedUser refreshToken(String refreshToken) {
        // Validar que sea un refresh token válido
        if (!jwtServicePort.canTokenBeRefreshed(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Validar que no esté expirado
        if (jwtServicePort.isTokenExpired(refreshToken)) {
            throw new InvalidTokenException("Refresh token expired. Please log in again.");
        }

        // Extraer información del refresh token
        String username = jwtServicePort.extractUsername(refreshToken);
        String email = jwtServicePort.getEmailFromToken(refreshToken);

        // Generar nuevos tokens
        String newAccessToken = jwtServicePort.generateToken(username);
        String newRefreshToken = jwtServicePort.generateRefreshToken(username);

        return AuthenticatedUser.builder()
                .username(username)
                .email(email)
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtExpirationTime)
                .build();
    }
}

