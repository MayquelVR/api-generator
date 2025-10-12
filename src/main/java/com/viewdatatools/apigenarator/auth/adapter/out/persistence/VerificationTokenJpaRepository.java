package com.viewdatatools.apigenarator.auth.adapter.out.persistence;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.VerificationTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenJpaRepository extends JpaRepository<VerificationTokenJpaEntity, Long> {
    Optional<VerificationTokenJpaEntity> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
}
