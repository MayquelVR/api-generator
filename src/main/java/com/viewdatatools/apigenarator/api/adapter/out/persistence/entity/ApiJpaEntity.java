package com.viewdatatools.apigenarator.api.adapter.out.persistence.entity;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "api")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String version;
    private String baseUrl;
    private String method;

    @Column(columnDefinition = "TEXT")
    private String requestFormat;

    @Column(columnDefinition = "TEXT")
    private String responseFormat;

    private String route;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;
}

