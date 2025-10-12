package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.exception.DocumentNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.UpdateDocumentUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateDocumentService implements UpdateDocumentUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final DocumentRepositoryPort documentRepositoryPort;
    private final SchemaValidatorService schemaValidatorService;

    @Override
    public DocumentDomain updateDocument(String username, String collectionName, UUID documentUuid, DocumentDomain updatedDocument) {
        // Find collection
        CollectionDomain collection = collectionRepositoryPort
                .findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));

        // Get existing document
        DocumentDomain existingDocument = documentRepositoryPort.findById(documentUuid)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with uuid: " + documentUuid));

        // Verify document belongs to the collection
        if (!existingDocument.getCollectionUuid().equals(collection.getUuid())) {
            throw new DocumentNotFoundException(
                    "Document " + documentUuid + " does not belong to collection '" + collectionName + "'"
            );
        }

        // Validate updated data against schema
        schemaValidatorService.validateDocument(updatedDocument.getData(), collection.getSchema());

        // Update document
        existingDocument.setData(updatedDocument.getData());
        existingDocument.setUpdatedAt(LocalDateTime.now());

        return documentRepositoryPort.save(existingDocument);
    }
}
