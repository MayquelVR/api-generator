package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.CollectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<CollectionJpaEntity, Long> {
    Optional<CollectionJpaEntity> findByUserUsernameAndCollectionName(String username, String collectionName);
    List<CollectionJpaEntity> findAllByUserUsername(String username);
    boolean existsByUserUsernameAndCollectionName(String username, String collectionName);
}

