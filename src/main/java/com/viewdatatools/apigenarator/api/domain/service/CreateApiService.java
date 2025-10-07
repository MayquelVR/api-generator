package com.viewdatatools.apigenarator.api.domain.service;


import com.viewdatatools.apigenarator.api.domain.exception.UserNotFoundException;
import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;
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
    public void createApi(ApiDomain apiDomain) {
        if (!userRepositoryPort.existsByUsername(apiDomain.getUsername())) {
            throw new UserNotFoundException("User not found: " + apiDomain.getUsername());
        }

        validateApiData(apiDomain);

        apiRepositoryPort.save(apiDomain);
    }

    private void validateApiData(ApiDomain apiDomain) {
        if (apiDomain.getName() == null || apiDomain.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la API no puede estar vacío");
        }
        if (apiDomain.getRoute() == null || apiDomain.getRoute().trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta de la API no puede estar vacía");
        }
        if (apiDomain.getMethod() == null || apiDomain.getMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("El método HTTP no puede estar vacío");
        }
    }
}
