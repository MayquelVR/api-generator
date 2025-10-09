package com.viewdatatools.apigenarator.collection.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain entity representing a user-defined collection with its schema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionDomain {
    private Long id;
    private String collectionName;
    private String username;
    private Map<String, FieldDefinition> schema;            // The collection schema definition
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
