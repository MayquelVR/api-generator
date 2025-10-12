package com.viewdatatools.apigenarator.collection.adapter.in.web.mapper;

import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import com.viewdatatools.apigenarator.collection.dto.CollectionResponse;
import com.viewdatatools.apigenarator.collection.dto.CollectionWithSchemaResponse;
import com.viewdatatools.apigenarator.collection.dto.CreateCollectionRequest;
import com.viewdatatools.apigenarator.collection.dto.FieldDefinitionDto;
import com.viewdatatools.apigenarator.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CollectionDtoMapper {

    private final DocumentRepositoryPort documentRepositoryPort;

    public CollectionDomain toDomain(CreateCollectionRequest request, UUID userUuid, String username) {
        return CollectionDomain.builder()
                .uuid(UuidUtil.validateOrGenerateUuidV7(request.getUuid()))
                .collectionName(request.getCollectionName())
                .userUuid(userUuid)
                .username(username)
                .schema(convertSchemaToFieldDefinitions(request.getSchema()))
                .build();
    }

    public CollectionResponse toResponse(CollectionDomain domain) {
        long documentCount = documentRepositoryPort.countByCollectionUuid(domain.getUuid());

        return CollectionResponse.builder()
                .uuid(domain.getUuid())
                .collectionName(domain.getCollectionName())
                .username(domain.getUsername())
                .documentCount(documentCount)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public CollectionWithSchemaResponse toResponseWithSchema(CollectionDomain domain) {
        long documentCount = documentRepositoryPort.countByCollectionUuid(domain.getUuid());

        return CollectionWithSchemaResponse.builder()
                .uuid(domain.getUuid())
                .collectionName(domain.getCollectionName())
                .username(domain.getUsername())
                .schema(convertFieldDefinitionsToDto(domain.getSchema()))
                .documentCount(documentCount)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Map<String, FieldDefinitionDto> convertFieldDefinitionsToDto(Map<String, FieldDefinition> domainSchema) {
        if (domainSchema == null) {
            return null;
        }

        Map<String, FieldDefinitionDto> result = new HashMap<>();
        for (Map.Entry<String, FieldDefinition> entry : domainSchema.entrySet()) {
            result.put(entry.getKey(), convertFieldDefinitionToDto(entry.getValue()));
        }
        return result;
    }

    private FieldDefinitionDto convertFieldDefinitionToDto(FieldDefinition domain) {
        if (domain == null) {
            return null;
        }

        FieldDefinitionDto dto = new FieldDefinitionDto();
        dto.setType(domain.getType());
        dto.setRequired(domain.getRequired());
        dto.setMaxLength(domain.getMaxLength());
        dto.setDefaultValue(domain.getDefaultValue());
        dto.setProperties(convertFieldDefinitionsToDto(domain.getProperties()));
        dto.setItems(convertFieldDefinitionToDto(domain.getItems()));
        return dto;
    }

    private Map<String, FieldDefinition> convertSchemaToFieldDefinitions(Map<String, FieldDefinitionDto> dtoSchema) {
        if (dtoSchema == null) {
            return null;
        }

        Map<String, FieldDefinition> result = new HashMap<>();
        for (Map.Entry<String, FieldDefinitionDto> entry : dtoSchema.entrySet()) {
            result.put(entry.getKey(), convertFieldDefinition(entry.getValue()));
        }
        return result;
    }

    private FieldDefinition convertFieldDefinition(FieldDefinitionDto dto) {
        if (dto == null) {
            return null;
        }

        return FieldDefinition.builder()
                .type(dto.getType())
                .required(dto.getRequired())
                .maxLength(dto.getMaxLength())
                .defaultValue(dto.getDefaultValue())
                .properties(convertSchemaToFieldDefinitions(dto.getProperties()))
                .items(convertFieldDefinition(dto.getItems()))
                .build();
    }
}
