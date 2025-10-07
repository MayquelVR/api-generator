package com.viewdatatools.apigenarator.api.domain.service;

import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;
import com.viewdatatools.apigenarator.api.domain.port.in.ListUserApisUseCase;
import com.viewdatatools.apigenarator.api.domain.port.out.ApiRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUserApisService implements ListUserApisUseCase {
    private final ApiRepositoryPort apiRepositoryPort;

    public ListUserApisService(ApiRepositoryPort apiRepositoryPort) {
        this.apiRepositoryPort = apiRepositoryPort;
    }

    @Override
    public List<ApiDomain> listApisByUsername(String username) {
        return apiRepositoryPort.findAllByUserUsername(username);
    }
}
