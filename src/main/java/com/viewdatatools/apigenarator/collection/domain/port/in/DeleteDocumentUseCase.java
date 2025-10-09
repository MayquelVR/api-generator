package com.viewdatatools.apigenarator.collection.domain.port.in;

/**
 * Use case for deleting a document
 */
public interface DeleteDocumentUseCase {
    void deleteDocument(String username, String collectionName, Long documentId);
}
