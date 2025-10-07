package com.viewdatatools.apigenarator.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para API personalizada (sin dependencias de infraestructura)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Api {
    private Long id;
    private String name;
    private String description;
    private String version;
    private String baseUrl;
    private String method;
    private String requestFormat;
    private String responseFormat;
    private String route;
    private String username;
}
