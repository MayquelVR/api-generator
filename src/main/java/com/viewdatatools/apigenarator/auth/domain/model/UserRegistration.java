package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class UserRegistration {
    private final UUID uuid; // UUID opcional del frontend
    private final String username;
    private final String email;

    @ToString.Exclude  // Excluir del toString() para evitar logs accidentales
    private final String password;
}
