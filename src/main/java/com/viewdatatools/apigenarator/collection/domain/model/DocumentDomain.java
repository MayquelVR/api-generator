package com.viewdatatools.apigenarator.collection.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing a document stored in a collection
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDomain {
    private UUID uuid;
    private UUID collectionUuid;
    private Map<String, Object> data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
