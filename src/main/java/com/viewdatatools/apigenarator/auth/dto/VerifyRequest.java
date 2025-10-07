package com.viewdatatools.apigenarator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank(message = "Token is required")
    private String token;
}
