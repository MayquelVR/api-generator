package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.DocumentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentJpaEntity, Long> {
    List<DocumentJpaEntity> findAllByCollectionId(Long collectionId);
    void deleteAllByCollectionId(Long collectionId);
    long countByCollectionId(Long collectionId);
}
