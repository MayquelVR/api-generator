package com.viewdatatools.apigenarator.api.domain.service;

import com.viewdatatools.apigenarator.api.domain.exception.ApiNotFoundException;
import com.viewdatatools.apigenarator.api.domain.exception.InvalidHttpMethodException;
import com.viewdatatools.apigenarator.api.domain.exception.InvalidRequestFormatException;
import com.viewdatatools.apigenarator.api.domain.model.Api;
import com.viewdatatools.apigenarator.api.domain.port.in.InvokeApiUseCase;
import com.viewdatatools.apigenarator.api.domain.port.out.ApiRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class InvokeApiService implements InvokeApiUseCase {
    private final ApiRepositoryPort apiRepositoryPort;

    public InvokeApiService(ApiRepositoryPort apiRepositoryPort) {
        this.apiRepositoryPort = apiRepositoryPort;
    }

    @Override
    public Object invokeApi(String username, String route, String httpMethod, Object body) {
        Api api = apiRepositoryPort.findByUsernameAndRoute(username, route)
                .orElseThrow(() -> new ApiNotFoundException("API not found for user: " + username + " and route: " + route));

        if (!api.getMethod().equalsIgnoreCase(httpMethod)) {
            throw new InvalidHttpMethodException("Método HTTP no permitido para esta API. Esperado: " + api.getMethod() + ", Recibido: " + httpMethod);
        }

        if (api.getRequestFormat() != null && body != null) {
            if (!(body instanceof java.util.Map)) {
                throw new InvalidRequestFormatException("El formato del request no es válido. Se esperaba un objeto JSON.");
            }
        }

        return api.getResponseFormat() != null ? api.getResponseFormat() : "API invocada correctamente";
    }
}
