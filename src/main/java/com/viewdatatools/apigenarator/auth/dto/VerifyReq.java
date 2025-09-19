package com.viewdatatools.apigenarator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyReq {
    @NotBlank(message = "Password is required")
    private String token;
}