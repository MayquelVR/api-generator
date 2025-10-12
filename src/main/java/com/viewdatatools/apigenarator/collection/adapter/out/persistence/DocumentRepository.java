package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.DocumentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentJpaEntity, UUID> {
    List<DocumentJpaEntity> findAllByCollectionUuid(UUID collectionUuid);
    void deleteAllByCollectionUuid(UUID collectionUuid);
    long countByCollectionUuid(UUID collectionUuid);
}
