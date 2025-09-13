package com.viewdatatools.apigenarator.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginReq {
    @NotBlank(message = "The username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    private String password;
}
