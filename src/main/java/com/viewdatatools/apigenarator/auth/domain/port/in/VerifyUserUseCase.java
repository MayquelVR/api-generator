package com.viewdatatools.apigenarator.auth.domain.port.in;

import com.viewdatatools.apigenarator.auth.domain.model.VerificationToken;

public interface VerifyUserUseCase {
    void verify(VerificationToken verificationToken);
}
