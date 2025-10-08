package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.PasswordReset;

public interface ResetPasswordUseCase {
    void resetPassword(PasswordReset passwordReset);
}
