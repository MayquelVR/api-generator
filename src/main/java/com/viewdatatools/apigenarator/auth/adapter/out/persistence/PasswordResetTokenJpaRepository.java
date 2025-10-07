package com.viewdatatools.apigenarator.auth.adapter.out.persistence;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.PasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, Long> {
    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
}

