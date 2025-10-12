package com.viewdatatools.apigenarator.collection.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionDomain {
    private UUID uuid;
    private String collectionName;
    private UUID userUuid;
    private String username;
    private Map<String, FieldDefinition> schema;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
