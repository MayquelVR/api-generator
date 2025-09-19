package com.viewdatatools.apigenarator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordReq {
    @NotBlank(message = "Password is required")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    private String password;
}
