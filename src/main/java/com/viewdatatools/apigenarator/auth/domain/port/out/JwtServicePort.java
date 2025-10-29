package com.viewdatatools.apigenarator.auth.domain.port.out;

public interface JwtServicePort {
    String generateToken(String username);
    String generateRefreshToken(String username);
    boolean canTokenBeRefreshed(String token);
    boolean isTokenExpired(String token);
    String extractUsername(String token);
    String getEmailFromToken(String token);
}
