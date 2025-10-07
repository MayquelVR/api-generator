package com.viewdatatools.apigenarator.api.domain.service;

import com.viewdatatools.apigenarator.api.domain.port.in.ListUserApisUseCase;
import com.viewdatatools.apigenarator.api.domain.port.out.ApiRepositoryPort;
import com.viewdatatools.apigenarator.api.domain.model.Api;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUserApisService implements ListUserApisUseCase {
    private final ApiRepositoryPort apiRepositoryPort;

    public ListUserApisService(ApiRepositoryPort apiRepositoryPort) {
        this.apiRepositoryPort = apiRepositoryPort;
    }

    @Override
    public List<Api> listApisByUsername(String username) {
        return apiRepositoryPort.findAllByUserUsername(username);
    }
}
