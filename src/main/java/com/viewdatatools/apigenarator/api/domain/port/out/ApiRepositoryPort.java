package com.viewdatatools.apigenarator.api.domain.port.out;

import com.viewdatatools.apigenarator.api.domain.model.Api;

import java.util.List;
import java.util.Optional;

public interface ApiRepositoryPort {
    void save(Api api);
    Optional<Api> findByUsernameAndRoute(String username, String route);
    List<Api> findAllByUserUsername(String username);
}
