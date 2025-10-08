package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegistration {
    private final String username;
    private final String email;
    private final String password;
}
