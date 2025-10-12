package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionAlreadyExistsException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.CreateCollectionUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateCollectionService implements CreateCollectionUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final SchemaValidatorService schemaValidatorService;

    @Override
    public CollectionDomain createCollection(CollectionDomain collection) {
        if (collection.getCollectionName() == null || collection.getCollectionName().trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be empty");
        }

        if (collectionRepositoryPort.existsByUsernameAndCollectionName(
                collection.getUsername(), collection.getCollectionName())) {
            throw new CollectionAlreadyExistsException(
                    "Collection '" + collection.getCollectionName() + "' already exists for user '" + collection.getUsername() + "'"
            );
        }

        schemaValidatorService.validateSchema(collection.getSchema());

        LocalDateTime now = LocalDateTime.now();
        collection.setCreatedAt(now);
        collection.setUpdatedAt(now);

        return collectionRepositoryPort.save(collection);
    }
}

