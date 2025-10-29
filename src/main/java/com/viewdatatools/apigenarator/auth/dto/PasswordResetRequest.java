package com.viewdatatools.apigenarator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordResetRequest {
    @NotBlank(message = "Password is required")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    @ToString.Exclude  // Excluir del toString() para evitar logs accidentales
    private String password;
}
