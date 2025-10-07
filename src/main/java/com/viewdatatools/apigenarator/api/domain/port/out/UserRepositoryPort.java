package com.viewdatatools.apigenarator.api.domain.port.out;

public interface UserRepositoryPort {
    boolean existsByUsername(String username);
}

