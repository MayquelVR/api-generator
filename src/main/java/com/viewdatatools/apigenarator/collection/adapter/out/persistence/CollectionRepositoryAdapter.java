package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewdatatools.apigenarator.auth.adapter.out.persistence.UserJpaRepository;
import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.UserJpaEntity;
import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.CollectionJpaEntity;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CollectionRepositoryAdapter implements CollectionRepositoryPort {

    private final CollectionRepository collectionRepository;
    private final UserJpaRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CollectionDomain save(CollectionDomain collection) {
        UserJpaEntity user = userRepository.findByUsername(collection.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + collection.getUsername()));

        Map<String, Object> schemaMap = objectMapper.convertValue(
                collection.getSchema(),
                new TypeReference<Map<String, Object>>() {}
        );

        CollectionJpaEntity entity = CollectionJpaEntity.builder()
                .id(collection.getId())
                .collectionName(collection.getCollectionName())
                .user(user)
                .schema(schemaMap)
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();

        CollectionJpaEntity saved = collectionRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CollectionDomain> findByUsernameAndCollectionName(String username, String collectionName) {
        return collectionRepository.findByUserUsernameAndCollectionName(username, collectionName)
                .map(this::toDomain);
    }

    @Override
    public List<CollectionDomain> findAllByUsername(String username) {
        return collectionRepository.findAllByUserUsername(username).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long collectionId) {
        collectionRepository.deleteById(collectionId);
    }

    @Override
    public boolean existsByUsernameAndCollectionName(String username, String collectionName) {
        return collectionRepository.existsByUserUsernameAndCollectionName(username, collectionName);
    }

    private CollectionDomain toDomain(CollectionJpaEntity entity) {
        Map<String, FieldDefinition> schema = objectMapper.convertValue(
                entity.getSchema(),
                new TypeReference<Map<String, FieldDefinition>>() {}
        );

        return CollectionDomain.builder()
                .id(entity.getId())
                .collectionName(entity.getCollectionName())
                .username(entity.getUser().getUsername())
                .schema(schema)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

