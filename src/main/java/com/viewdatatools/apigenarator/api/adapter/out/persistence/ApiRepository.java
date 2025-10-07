package com.viewdatatools.apigenarator.api.adapter.out.persistence;

import com.viewdatatools.apigenarator.api.adapter.out.persistence.entity.ApiJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiRepository extends JpaRepository<ApiJpaEntity, Long> {
    Optional<ApiJpaEntity> findByUserUsernameAndRoute(String username, String route);

    List<ApiJpaEntity> findAllByUserUsername(String username);
}
