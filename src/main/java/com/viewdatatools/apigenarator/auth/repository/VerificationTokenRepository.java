package com.viewdatatools.apigenarator.auth.repository;

import com.viewdatatools.apigenarator.auth.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTokenHash(String tokenHash);
}