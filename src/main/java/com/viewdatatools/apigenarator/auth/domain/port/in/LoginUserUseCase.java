package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.AuthenticatedUser;
import com.viewdatatools.apigenarator.auth.domain.model.LoginCredentials;

public interface LoginUserUseCase {
    AuthenticatedUser login(LoginCredentials credentials);
}
