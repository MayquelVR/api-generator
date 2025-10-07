package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.dto.LoginRequest;
import com.viewdatatools.apigenarator.auth.dto.LoginResponse;

public interface LoginUserUseCase {
    LoginResponse login(LoginRequest request);
}
