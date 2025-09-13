package com.viewdatatools.apigenarator.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID token;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);
}