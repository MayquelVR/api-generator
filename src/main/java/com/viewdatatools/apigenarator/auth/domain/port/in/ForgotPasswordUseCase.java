package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.ForgotPassword;

public interface ForgotPasswordUseCase {
    void forgotPassword(ForgotPassword forgotPassword);
}
