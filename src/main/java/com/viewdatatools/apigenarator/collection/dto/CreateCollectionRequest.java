package com.viewdatatools.apigenarator.collection.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateCollectionRequest {
    private UUID uuid;
    private String collectionName;
    private Map<String, FieldDefinitionDto> schema;
}
