package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.GetCollectionUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCollectionService implements GetCollectionUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;

    @Override
    public CollectionDomain getCollection(String username, String collectionName) {
        return collectionRepositoryPort.findByUsernameAndCollectionName(username, collectionName)
                .orElseThrow(() -> new CollectionNotFoundException(
                        "Collection '" + collectionName + "' not found for user '" + username + "'"
                ));
    }
}

