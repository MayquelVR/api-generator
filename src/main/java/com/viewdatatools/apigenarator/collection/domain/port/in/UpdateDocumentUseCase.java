package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

public interface UpdateDocumentUseCase {
    DocumentDomain updateDocument(String username, String collectionName, Long documentId, DocumentDomain document);
}
