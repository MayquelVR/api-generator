package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordResetRequest {
    private final String email;
}

