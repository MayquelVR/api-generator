package com.viewdatatools.apigenarator.auth.adapter.out.persistence;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.VerificationTokenJpaEntity;
import com.viewdatatools.apigenarator.auth.domain.port.out.VerificationTokenPort;
import com.viewdatatools.apigenarator.auth.domain.exception.InvalidTokenException;
import com.viewdatatools.apigenarator.auth.domain.exception.TokenHasExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VerificationTokenAdapter implements VerificationTokenPort {

    private final VerificationTokenJpaRepository repository;

    @Override
    public void create(String username, String email, String password, String tokenHash) {
        VerificationTokenJpaEntity entity = VerificationTokenJpaEntity.builder()
                .username(username)
                .email(email)
                .password(password)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        repository.save(entity);
    }

    @Override
    public VerificationTokenData validate(String tokenHash) {
        VerificationTokenJpaEntity entity = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenHasExpiredException("Token has expired");
        }

        return new VerificationTokenData(
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword()
        );
    }

    @Override
    @Transactional
    public void delete(String tokenHash) {
        repository.deleteByTokenHash(tokenHash);
    }
}
