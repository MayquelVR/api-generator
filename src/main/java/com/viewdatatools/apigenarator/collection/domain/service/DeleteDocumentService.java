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

@Service
@RequiredArgsConstructor
public class DeleteDocumentService implements DeleteDocumentUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final DocumentRepositoryPort documentRepositoryPort;

    @Override
    public void deleteDocument(String username, String collectionName, Long documentId) {
        // Verify collection exists and belongs to user
        CollectionDomain collection = collectionRepositoryPort
                .findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));

        // Get document to verify it exists
        DocumentDomain document = documentRepositoryPort.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        // Verify document belongs to the collection
        if (!document.getCollectionId().equals(collection.getId())) {
            throw new DocumentNotFoundException(
                    "Document " + documentId + " does not belong to collection '" + collectionName + "'"
            );
        }

        // Delete document
        documentRepositoryPort.deleteById(documentId);
    }
}
