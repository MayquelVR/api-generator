package com.viewdatatools.apigenarator.auth.adapter.out.persistence;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.PasswordResetTokenJpaEntity;
import com.viewdatatools.apigenarator.auth.domain.port.out.PasswordResetTokenPort;
import com.viewdatatools.apigenarator.auth.domain.exception.InvalidTokenException;
import com.viewdatatools.apigenarator.auth.domain.exception.TokenHasExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenAdapter implements PasswordResetTokenPort {

    private final PasswordResetTokenJpaRepository repository;

    @Override
    public void create(String email, String tokenHash) {
        PasswordResetTokenJpaEntity entity = PasswordResetTokenJpaEntity.builder()
                .email(email)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        repository.save(entity);
    }

    @Override
    public String validateAndGetEmail(String tokenHash) {
        PasswordResetTokenJpaEntity entity = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenHasExpiredException("Token has expired");
        }

        return entity.getEmail();
    }

    @Override
    @Transactional
    public void delete(String tokenHash) {
        repository.deleteByTokenHash(tokenHash);
    }
}
