package com.viewdatatools.apigenarator.api.domain.port.in;

public interface InvokeApiUseCase {
    Object invokeApi(String username, String route, String httpMethod, Object body);
}

