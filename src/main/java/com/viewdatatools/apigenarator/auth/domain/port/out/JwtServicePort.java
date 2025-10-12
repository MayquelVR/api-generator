package com.viewdatatools.apigenarator.auth.domain.port.out;

public interface JwtServicePort {
    String generateToken(String username);
    String generateRefreshToken(String username);
}
