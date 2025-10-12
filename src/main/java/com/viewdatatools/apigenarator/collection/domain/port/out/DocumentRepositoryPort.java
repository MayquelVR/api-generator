package com.viewdatatools.apigenarator.collection.domain.port.out;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for document persistence operations
 */
public interface DocumentRepositoryPort {
    DocumentDomain save(DocumentDomain document);
    Optional<DocumentDomain> findById(UUID documentUuid);
    List<DocumentDomain> findAllByCollectionUuid(UUID collectionUuid);
    void deleteById(UUID documentUuid);
    void deleteAllByCollectionUuid(UUID collectionUuid);
    long countByCollectionUuid(UUID collectionUuid);
}
