package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.port.in.RegisterUserUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.*;
import com.viewdatatools.apigenarator.auth.dto.RegisterRequest;
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

    @Value("${app.base-url:http://localhost:4200/activate-account}")
    private String activateAccountBaseUrl;

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
    public void register(RegisterRequest request) {
        if (userRepositoryPort.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepositoryPort.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String encodedPassword = passwordEncoderPort.encode(request.getPassword());

        String token = tokenServicePort.generateToken();
        String tokenHash = tokenServicePort.hash(token);

        verificationTokenPort.create(request.getUsername(), request.getEmail(), encodedPassword, tokenHash);

        mailServicePort.sendMail(
                request.getEmail(),
                "Activate your account",
                "Welcome " + request.getUsername() + "!\n\nActivate your account by clicking the following link:\n"
                + activateAccountBaseUrl + "?token=" + token
        );
    }
}
