package com.viewdatatools.apigenarator.users.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class VerifyReq {
    @NotNull(message = "Password is required")
    private UUID token;
}