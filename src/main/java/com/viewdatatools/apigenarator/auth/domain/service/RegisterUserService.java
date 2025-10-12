package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.model.UserRegistration;
import com.viewdatatools.apigenarator.auth.domain.port.in.RegisterUserUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.*;
import com.viewdatatools.apigenarator.auth.domain.exception.EmailAlreadyExistsException;
import com.viewdatatools.apigenarator.auth.domain.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenServicePort tokenServicePort;
    private final VerificationTokenPort verificationTokenPort;
    private final MailServicePort mailServicePort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Value("${app.ui-base-url:http://localhost:4200}")
    private String uiBaseUrl;

    @Value("${app.activate-account-url:/activate-account}")
    private String activateAccountPath;

    public RegisterUserService(UserRepositoryPort userRepositoryPort,
                               TokenServicePort tokenServicePort,
                               VerificationTokenPort verificationTokenPort,
                               MailServicePort mailServicePort,
                               PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenServicePort = tokenServicePort;
        this.verificationTokenPort = verificationTokenPort;
        this.mailServicePort = mailServicePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void register(UserRegistration userRegistration) {
        if (userRepositoryPort.existsByUsername(userRegistration.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepositoryPort.existsByEmail(userRegistration.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String encodedPassword = passwordEncoderPort.encode(userRegistration.getPassword());

        String token = tokenServicePort.generateToken();
        String tokenHash = tokenServicePort.hash(token);

        verificationTokenPort.create(
                userRegistration.getUuid(),
                userRegistration.getUsername(),
                userRegistration.getEmail(),
                encodedPassword,
                tokenHash
        );

        mailServicePort.sendMail(
                userRegistration.getEmail(),
                "Activate your account",
                "Welcome " + userRegistration.getUsername() + "!\n\nActivate your account by clicking the following link:\n"
                + uiBaseUrl + activateAccountPath + "?token=" + token
        );
    }
}
