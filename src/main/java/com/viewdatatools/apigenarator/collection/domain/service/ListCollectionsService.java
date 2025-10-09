package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.ListCollectionsUseCase;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCollectionsService implements ListCollectionsUseCase {

    private final CollectionRepositoryPort collectionRepositoryPort;

    @Override
    public List<CollectionDomain> listCollections(String username) {
        return collectionRepositoryPort.findAllByUsername(username);
    }
}

