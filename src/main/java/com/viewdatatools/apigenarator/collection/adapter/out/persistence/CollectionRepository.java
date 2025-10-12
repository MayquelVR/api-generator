package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.CollectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends JpaRepository<CollectionJpaEntity, UUID> {
    Optional<CollectionJpaEntity> findByUserUuidAndCollectionName(UUID userUuid, String collectionName);
    List<CollectionJpaEntity> findAllByUserUuid(UUID userUuid);
    boolean existsByUserUuidAndCollectionName(UUID userUuid, String collectionName);
}
