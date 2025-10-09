package com.viewdatatools.apigenarator.collection.adapter.in.web.mapper;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.dto.CreateDocumentRequest;
import com.viewdatatools.apigenarator.collection.dto.DocumentResponse;
import com.viewdatatools.apigenarator.collection.dto.UpdateDocumentRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Document domain objects and DTOs
 */
@Component
public class DocumentDtoMapper {

    /**
     * Convert DocumentDomain to DocumentResponse DTO
     */
    public DocumentResponse toResponse(DocumentDomain domain) {
        if (domain == null) {
            return null;
        }

        return DocumentResponse.builder()
                .id(domain.getId())
                .collectionId(domain.getCollectionId())
                .data(domain.getData())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of DocumentDomain to list of DocumentResponse DTOs
     */
    public List<DocumentResponse> toResponseList(List<DocumentDomain> domains) {
        if (domains == null) {
            return null;
        }

        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert CreateDocumentRequest DTO to DocumentDomain
     */
    public DocumentDomain toDomain(CreateDocumentRequest request, Long collectionId) {
        if (request == null) {
            return null;
        }

        return DocumentDomain.builder()
                .collectionId(collectionId)
                .data(request.getData())
                .build();
    }

    /**
     * Update DocumentDomain with data from UpdateDocumentRequest
     */
    public DocumentDomain updateDomain(DocumentDomain existing, UpdateDocumentRequest request) {
        if (existing == null || request == null) {
            return existing;
        }

        existing.setData(request.getData());
        return existing;
    }
}

