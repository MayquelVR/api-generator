package com.viewdatatools.apigenarator.collection.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CollectionResponse {
    private Long id;
    private String collectionName;
    private String username;
    private Long documentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

