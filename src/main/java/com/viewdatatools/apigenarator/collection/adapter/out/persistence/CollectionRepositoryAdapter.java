package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewdatatools.apigenarator.auth.adapter.out.persistence.UserJpaRepository;
import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.CollectionJpaEntity;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CollectionRepositoryAdapter implements CollectionRepositoryPort {

    private final CollectionRepository collectionRepository;
    private final UserJpaRepository userJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CollectionDomain save(CollectionDomain collection) {
        Map<String, Object> schemaMap = objectMapper.convertValue(
                collection.getSchema(),
                new TypeReference<Map<String, Object>>() {}
        );

        CollectionJpaEntity entity = CollectionJpaEntity.builder()
                .uuid(collection.getUuid())
                .collectionName(collection.getCollectionName())
                .userUuid(collection.getUserUuid())
                .schema(schemaMap)
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();

        CollectionJpaEntity saved = collectionRepository.save(entity);
        return toDomain(saved, collection.getUsername());
    }

    @Override
    public Optional<CollectionDomain> findByUsernameAndCollectionName(String username, String collectionName) {
        UUID userUuid = getUserUuidFromUsername(username);
        return collectionRepository.findByUserUuidAndCollectionName(userUuid, collectionName)
                .map(entity -> toDomain(entity, username));
    }

    @Override
    public List<CollectionDomain> findAllByUsername(String username) {
        UUID userUuid = getUserUuidFromUsername(username);
        return collectionRepository.findAllByUserUuid(userUuid).stream()
                .map(entity -> toDomain(entity, username))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID collectionUuid) {
        collectionRepository.deleteById(collectionUuid);
    }

    @Override
    public boolean existsByUsernameAndCollectionName(String username, String collectionName) {
        UUID userUuid = getUserUuidFromUsername(username);
        return collectionRepository.existsByUserUuidAndCollectionName(userUuid, collectionName);
    }

    private UUID getUserUuidFromUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getUuid();
    }

    private CollectionDomain toDomain(CollectionJpaEntity entity, String username) {
        Map<String, FieldDefinition> schema = objectMapper.convertValue(
                entity.getSchema(),
                new TypeReference<Map<String, FieldDefinition>>() {}
        );

        return CollectionDomain.builder()
                .uuid(entity.getUuid())
                .collectionName(entity.getCollectionName())
                .userUuid(entity.getUserUuid())
                .username(username)
                .schema(schema)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
