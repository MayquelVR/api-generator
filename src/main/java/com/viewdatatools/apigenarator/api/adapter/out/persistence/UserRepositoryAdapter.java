package com.viewdatatools.apigenarator.api.adapter.out.persistence;

import com.viewdatatools.apigenarator.api.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}

