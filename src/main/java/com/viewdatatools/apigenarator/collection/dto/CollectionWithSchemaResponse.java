package com.viewdatatools.apigenarator.collection.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CollectionWithSchemaResponse {
    private UUID uuid;
    private String collectionName;
    private String username;
    private Map<String, FieldDefinitionDto> schema;
    private Long documentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
