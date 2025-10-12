package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

import java.util.UUID;

public interface GetDocumentUseCase {
    DocumentDomain getDocument(String username, String collectionName, UUID documentUuid);
}
