package com.viewdatatools.apigenarator.collection.domain.port.out;

import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;

import java.util.List;
import java.util.Optional;

/**
 * Output port for document persistence operations
 */
public interface DocumentRepositoryPort {
    DocumentDomain save(DocumentDomain document);
    Optional<DocumentDomain> findById(Long documentId);
    List<DocumentDomain> findAllByCollectionId(Long collectionId);
    void deleteById(Long documentId);
    void deleteAllByCollectionId(Long collectionId);
    long countByCollectionId(Long collectionId);
}

