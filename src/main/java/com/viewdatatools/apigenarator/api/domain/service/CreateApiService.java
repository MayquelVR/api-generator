package com.viewdatatools.apigenarator.api.domain.service;


import com.viewdatatools.apigenarator.api.domain.exception.UserNotFoundException;
import com.viewdatatools.apigenarator.api.domain.model.Api;
import com.viewdatatools.apigenarator.api.domain.port.in.CreateApiUseCase;
import com.viewdatatools.apigenarator.api.domain.port.out.ApiRepositoryPort;
import com.viewdatatools.apigenarator.api.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateApiService implements CreateApiUseCase {

    private final ApiRepositoryPort apiRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public CreateApiService(ApiRepositoryPort apiRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.apiRepositoryPort = apiRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void createApi(Api api) {
        if (!userRepositoryPort.existsByUsername(api.getUsername())) {
            throw new UserNotFoundException("User not found: " + api.getUsername());
        }

        validateApiData(api);

        apiRepositoryPort.save(api);
    }

    private void validateApiData(Api api) {
        if (api.getName() == null || api.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la API no puede estar vacío");
        }
        if (api.getRoute() == null || api.getRoute().trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta de la API no puede estar vacía");
        }
        if (api.getMethod() == null || api.getMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("El método HTTP no puede estar vacío");
        }
    }
}
