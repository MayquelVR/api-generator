package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.exception.DocumentNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.DeleteDocumentUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteDocumentService implements DeleteDocumentUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final DocumentRepositoryPort documentRepositoryPort;

    @Override
    public void deleteDocument(String username, String collectionName, UUID documentUuid) {
        // Verify collection exists and belongs to user
        CollectionDomain collection = collectionRepositoryPort
                .findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));

        // Get document to verify it exists
        DocumentDomain document = documentRepositoryPort.findById(documentUuid)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with uuid: " + documentUuid));

        // Verify document belongs to the collection
        if (!document.getCollectionUuid().equals(collection.getUuid())) {
            throw new DocumentNotFoundException(
                    "Document " + documentUuid + " does not belong to collection '" + collectionName + "'"
            );
        }

        // Delete document
        documentRepositoryPort.deleteById(documentUuid);
    }
}
