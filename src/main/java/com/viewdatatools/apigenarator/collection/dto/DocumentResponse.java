package com.viewdatatools.apigenarator.collection.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class DocumentResponse {
    private Long id;
    private Long collectionId;
    private Map<String, Object> data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
