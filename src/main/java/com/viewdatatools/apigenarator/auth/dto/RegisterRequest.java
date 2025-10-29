package com.viewdatatools.apigenarator.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class RegisterRequest {

    private UUID uuid; // Campo opcional - si viene del frontend se usa, sino se genera automáticamente

    @NotBlank(message = "The username is required")
    private String username;

    @NotBlank(message = "The email is required")
    @Email(message = "The email is not in a valid format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    @ToString.Exclude  // Excluir del toString() para evitar logs accidentales
    private String password;
}
