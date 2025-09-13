package com.viewdatatools.apigenarator.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordReq {
    @NotBlank(message = "The email is required")
    @Email(message = "The email is not in a valid format")
    private String email;
}
