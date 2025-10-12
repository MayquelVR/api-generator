package com.viewdatatools.apigenarator.collection.domain.port.in;

import java.util.UUID;

/**
 * Use case for deleting a document
 */
public interface DeleteDocumentUseCase {
    void deleteDocument(String username, String collectionName, UUID documentUuid);
}
