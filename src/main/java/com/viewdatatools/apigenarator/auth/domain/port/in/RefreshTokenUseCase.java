package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;

public interface RefreshTokenUseCase {
    AuthenticatedUser refreshToken(String refreshToken);
}

