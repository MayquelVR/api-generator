package com.viewdatatools.apigenarator.collection.domain.port.out;

import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;

import java.util.List;
import java.util.Optional;

public interface CollectionRepositoryPort {
    CollectionDomain save(CollectionDomain collection);
    Optional<CollectionDomain> findByUsernameAndCollectionName(String username, String collectionName);
    List<CollectionDomain> findAllByUsername(String username);
    void delete(Long collectionId);
    boolean existsByUsernameAndCollectionName(String username, String collectionName);
}

