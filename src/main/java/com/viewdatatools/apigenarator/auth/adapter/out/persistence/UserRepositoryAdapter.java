package com.viewdatatools.apigenarator.auth.adapter.out.persistence;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.UserJpaEntity;
import com.viewdatatools.apigenarator.auth.domain.model.UserDomain;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public void save(UserDomain user) {
        // Generar UUID si no existe
        if (user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }

        UserJpaEntity jpaEntity = toJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
        user.setUuid(savedEntity.getUuid());
    }

    @Override
    public Optional<UserDomain> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(this::toDomain);
    }

    @Override
    public Optional<UserDomain> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    private UserJpaEntity toJpaEntity(UserDomain domain) {
        return UserJpaEntity.builder()
                .uuid(domain.getUuid())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private UserDomain toDomain(UserJpaEntity jpaEntity) {
        return UserDomain.builder()
                .uuid(jpaEntity.getUuid())
                .username(jpaEntity.getUsername())
                .email(jpaEntity.getEmail())
                .password(jpaEntity.getPassword())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
}
