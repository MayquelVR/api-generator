package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.UserRegistration;

public interface RegisterUserUseCase {
    void register(UserRegistration userRegistration);
}
