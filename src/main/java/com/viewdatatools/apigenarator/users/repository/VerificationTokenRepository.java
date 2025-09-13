package com.viewdatatools.apigenarator.users.repository;

import com.viewdatatools.apigenarator.users.model.User;
import com.viewdatatools.apigenarator.users.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(UUID token);
}