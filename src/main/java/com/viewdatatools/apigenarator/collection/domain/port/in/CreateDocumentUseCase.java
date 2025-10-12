package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

/**
 * Use case for creating a document in a collection
 */
public interface CreateDocumentUseCase {
    DocumentDomain createDocument(String username, String collectionName, DocumentDomain document);
}

