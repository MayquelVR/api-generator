package com.viewdatatools.apigenarator.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDomain {
    private UUID uuid;
    private String username;
    private String email;

    @ToString.Exclude  // Excluir del toString() para evitar logs accidentales
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
