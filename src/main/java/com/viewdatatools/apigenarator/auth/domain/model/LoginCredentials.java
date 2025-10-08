package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCredentials {
    private final String username;
    private final String password;
}

