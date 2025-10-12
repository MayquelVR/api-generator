package com.viewdatatools.apigenarator.auth.domain.port.out;

import com.viewdatatools.apigenarator.auth.domain.model.UserDomain;
import java.util.Optional;

public interface UserRepositoryPort {
    void save(UserDomain user);
    Optional<UserDomain> findByUsername(String username);
    Optional<UserDomain> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

