package com.viewdatatools.apigenarator.collection.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CollectionResponse {
    private UUID uuid;
    private String collectionName;
    private String username;
    private Long documentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
