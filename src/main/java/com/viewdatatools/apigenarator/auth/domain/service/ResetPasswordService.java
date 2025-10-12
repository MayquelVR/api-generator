package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.model.PasswordReset;
import com.viewdatatools.apigenarator.auth.domain.model.UserDomain;
import com.viewdatatools.apigenarator.auth.domain.port.in.ResetPasswordUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.PasswordEncoderPort;
import com.viewdatatools.apigenarator.auth.domain.port.out.PasswordResetTokenPort;
import com.viewdatatools.apigenarator.auth.domain.port.out.TokenServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.auth.domain.exception.EmailNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResetPasswordService implements ResetPasswordUseCase {

    private final TokenServicePort tokenServicePort;
    private final PasswordResetTokenPort passwordResetTokenPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public ResetPasswordService(TokenServicePort tokenServicePort,
                                PasswordResetTokenPort passwordResetTokenPort,
                                UserRepositoryPort userRepositoryPort,
                                PasswordEncoderPort passwordEncoderPort) {
        this.tokenServicePort = tokenServicePort;
        this.passwordResetTokenPort = passwordResetTokenPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void resetPassword(PasswordReset passwordReset) {
        String tokenHash = tokenServicePort.hash(passwordReset.getToken());
        String email = passwordResetTokenPort.validateAndGetEmail(tokenHash);

        UserDomain user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));

        String encodedPassword = passwordEncoderPort.encode(passwordReset.getNewPassword());

        user.setPassword(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userRepositoryPort.save(user);

        passwordResetTokenPort.delete(tokenHash);
    }
}
