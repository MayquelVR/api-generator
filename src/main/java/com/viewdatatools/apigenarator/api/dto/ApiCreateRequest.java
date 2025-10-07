package com.viewdatatools.apigenarator.api.dto;

import lombok.Data;

@Data
public class ApiCreateRequest {
    private String name;
    private String description;
    private String version;
    private String baseUrl;
    private String method;
    private String requestFormat;
    private String responseFormat;
    private String route;
}
