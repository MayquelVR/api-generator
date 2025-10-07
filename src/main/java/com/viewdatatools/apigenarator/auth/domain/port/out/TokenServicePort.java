package com.viewdatatools.apigenarator.auth.domain.port.out;

public interface TokenServicePort {
    String generateToken();
    String hash(String token);
}

