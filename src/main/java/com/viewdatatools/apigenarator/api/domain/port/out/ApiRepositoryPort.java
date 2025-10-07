package com.viewdatatools.apigenarator.api.domain.port.out;

import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;

import java.util.List;
import java.util.Optional;

public interface ApiRepositoryPort {
    void save(ApiDomain apiDomain);
    Optional<ApiDomain> findByUsernameAndRoute(String username, String route);
    List<ApiDomain> findAllByUserUsername(String username);
}
