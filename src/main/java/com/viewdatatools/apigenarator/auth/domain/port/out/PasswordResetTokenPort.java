package com.viewdatatools.apigenarator.auth.domain.port.out;

public interface PasswordResetTokenPort {
    void create(String email, String tokenHash);
    String validateAndGetEmail(String tokenHash);
    void delete(String tokenHash);
}

