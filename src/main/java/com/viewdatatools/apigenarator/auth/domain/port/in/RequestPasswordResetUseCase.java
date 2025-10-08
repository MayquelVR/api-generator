package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.PasswordResetRequest;

public interface RequestPasswordResetUseCase {
    void requestPasswordReset(PasswordResetRequest passwordResetRequest);
}
