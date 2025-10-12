package com.viewdatatools.apigenarator.collection.domain.port.in;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

import java.util.List;

public interface GetDocumentsUseCase {
    List<DocumentDomain> getDocuments(String username, String collectionName);
}

