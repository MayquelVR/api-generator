package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.CreateDocumentUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateDocumentService implements CreateDocumentUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final DocumentRepositoryPort documentRepositoryPort;
    private final SchemaValidatorService schemaValidatorService;

    @Override
    public DocumentDomain createDocument(String username, String collectionName, DocumentDomain document) {
        // Find collection
        CollectionDomain collection = collectionRepositoryPort
                .findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));

        // Validate document against schema
        schemaValidatorService.validateDocument(document.getData(), collection.getSchema());

        // Set collection ID and timestamps
        document.setCollectionId(collection.getId());
        LocalDateTime now = LocalDateTime.now();
        document.setCreatedAt(now);
        document.setUpdatedAt(now);

        return documentRepositoryPort.save(document);
    }
}

