package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticatedUser {
    private final String username;
    private final String email;
    private final String token;
    private final String refreshToken;
    private final long expiresIn;
}
