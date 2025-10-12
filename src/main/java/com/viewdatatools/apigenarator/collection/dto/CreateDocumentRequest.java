package com.viewdatatools.apigenarator.collection.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateDocumentRequest {
    private UUID uuid;
    private Map<String, Object> data;
}
