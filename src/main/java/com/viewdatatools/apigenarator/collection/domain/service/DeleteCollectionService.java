package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.DeleteCollectionUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCollectionService implements DeleteCollectionUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final DocumentRepositoryPort documentRepositoryPort;

    @Override
    public void deleteCollection(String username, String collectionName) {
        CollectionDomain collection = collectionRepositoryPort
                .findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));

        // Delete all documents in the collection
        documentRepositoryPort.deleteAllByCollectionId(collection.getId());

        // Delete the collection itself
        collectionRepositoryPort.delete(collection.getId());
    }
}

