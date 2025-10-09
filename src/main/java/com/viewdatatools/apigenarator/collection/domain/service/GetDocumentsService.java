package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.GetDocumentsUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDocumentsService implements GetDocumentsUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final DocumentRepositoryPort documentRepositoryPort;

    @Override
    public List<DocumentDomain> getDocuments(String username, String collectionName) {
        CollectionDomain collection = collectionRepositoryPort
                .findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));

        return documentRepositoryPort.findAllByCollectionId(collection.getId());
    }
}

