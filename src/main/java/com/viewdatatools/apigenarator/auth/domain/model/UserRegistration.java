package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserRegistration {
    private final UUID uuid; // UUID opcional del frontend
    private final String username;
    private final String email;
    private final String password;
}
