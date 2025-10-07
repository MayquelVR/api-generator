package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.dto.RegisterRequest;

public interface RegisterUserUseCase {
    void register(RegisterRequest request);
}
