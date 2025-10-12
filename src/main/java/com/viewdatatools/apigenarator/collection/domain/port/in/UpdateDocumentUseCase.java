package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

import java.util.UUID;

public interface UpdateDocumentUseCase {
    DocumentDomain updateDocument(String username, String collectionName, UUID documentUuid, DocumentDomain document);
}
