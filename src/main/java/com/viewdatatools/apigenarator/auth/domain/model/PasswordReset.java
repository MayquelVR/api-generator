package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordReset {
    private final String token;
    private final String newPassword;
}
