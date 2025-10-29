package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LoginCredentials {
    private final String username;

    @ToString.Exclude  // Excluir del toString() para evitar logs accidentales
    private final String password;
}
