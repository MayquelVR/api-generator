package com.viewdatatools.apigenarator.collection.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreateCollectionRequest {
    private String collectionName;
    private Map<String, FieldDefinitionDto> schema;
}

