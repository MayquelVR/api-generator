package com.viewdatatools.apigenarator.api.adapter.out.persistence;

import com.viewdatatools.apigenarator.api.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.auth.adapter.out.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userRepository;

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
