package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.model.PasswordResetRequest;
import com.viewdatatools.apigenarator.auth.domain.port.in.RequestPasswordResetUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.MailServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.PasswordResetTokenPort;
import com.viewdatatools.apigenarator.auth.domain.port.out.TokenServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.auth.domain.exception.EmailNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenServicePort tokenServicePort;
    private final PasswordResetTokenPort passwordResetTokenPort;
    private final MailServicePort mailServicePort;

    @Value("${app.ui-base-url:http://localhost:4200}")
    private String uiBaseUrl;

    @Value("${app.reset-password-url:/reset-password}")
    private String resetPasswordPath;

    public RequestPasswordResetService(UserRepositoryPort userRepositoryPort,
                                       TokenServicePort tokenServicePort,
                                       PasswordResetTokenPort passwordResetTokenPort,
                                       MailServicePort mailServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenServicePort = tokenServicePort;
        this.passwordResetTokenPort = passwordResetTokenPort;
        this.mailServicePort = mailServicePort;
    }

    @Override
    public void requestPasswordReset(PasswordResetRequest passwordResetRequest) {
        String email = passwordResetRequest.getEmail();

        userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));

        String token = tokenServicePort.generateToken();
        String tokenHash = tokenServicePort.hash(token);

        passwordResetTokenPort.create(email, tokenHash);

        mailServicePort.sendMail(
                email,
                "Reset your password",
                "Reset your password by clicking the following link:\n"
                + uiBaseUrl + resetPasswordPath + "?token=" + token
        );
    }
}
