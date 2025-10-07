package com.viewdatatools.apigenarator.auth.domain.port.in;

public interface RequestPasswordResetUseCase {
    void createPasswordResetToken(String email);
}

