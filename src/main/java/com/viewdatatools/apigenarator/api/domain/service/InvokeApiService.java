package com.viewdatatools.apigenarator.api.domain.service;

import com.viewdatatools.apigenarator.api.domain.exception.ApiNotFoundException;
import com.viewdatatools.apigenarator.api.domain.exception.InvalidHttpMethodException;
import com.viewdatatools.apigenarator.api.domain.exception.InvalidRequestFormatException;
import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;
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
        ApiDomain apiDomain = apiRepositoryPort.findByUsernameAndRoute(username, route)
                .orElseThrow(() -> new ApiNotFoundException("API not found for user: " + username + " and route: " + route));

        if (!apiDomain.getMethod().equalsIgnoreCase(httpMethod)) {
            throw new InvalidHttpMethodException("Método HTTP no permitido para esta API. Esperado: " + apiDomain.getMethod() + ", Recibido: " + httpMethod);
        }

        if (apiDomain.getRequestFormat() != null && body != null) {
            if (!(body instanceof java.util.Map)) {
                throw new InvalidRequestFormatException("El formato del request no es válido. Se esperaba un objeto JSON.");
            }
        }

        return apiDomain.getResponseFormat() != null ? apiDomain.getResponseFormat() : "API invocada correctamente";
    }
}
