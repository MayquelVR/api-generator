package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

public interface GetDocumentUseCase {
    DocumentDomain getDocument(String username, String collectionName, Long documentId);
}
