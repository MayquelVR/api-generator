package com.viewdatatools.apigenarator.auth.domain.service;

import com.viewdatatools.apigenarator.auth.domain.model.UserDomain;
import com.viewdatatools.apigenarator.auth.domain.model.VerificationToken;
import com.viewdatatools.apigenarator.auth.domain.port.in.VerifyUserUseCase;
import com.viewdatatools.apigenarator.auth.domain.port.out.TokenServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.auth.domain.port.out.VerificationTokenPort;
import com.viewdatatools.apigenarator.auth.domain.exception.EmailAlreadyExistsException;
import com.viewdatatools.apigenarator.auth.domain.exception.UsernameAlreadyExistsException;
import com.viewdatatools.apigenarator.util.UuidUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerifyUserService implements VerifyUserUseCase {

    private final TokenServicePort tokenServicePort;
    private final VerificationTokenPort verificationTokenPort;
    private final UserRepositoryPort userRepositoryPort;

    public VerifyUserService(TokenServicePort tokenServicePort,
                             VerificationTokenPort verificationTokenPort,
                             UserRepositoryPort userRepositoryPort) {
        this.tokenServicePort = tokenServicePort;
        this.verificationTokenPort = verificationTokenPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void verify(VerificationToken verificationToken) {
        String tokenHash = tokenServicePort.hash(verificationToken.getToken());
        VerificationTokenPort.VerificationTokenData tokenData = verificationTokenPort.validate(tokenHash);

        if (userRepositoryPort.existsByUsername(tokenData.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepositoryPort.existsByEmail(tokenData.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserDomain user = UserDomain.builder()
                .uuid(UuidUtil.validateOrGenerateUuidV7(tokenData.getUuid()))
                .username(tokenData.getUsername())
                .email(tokenData.getEmail())
                .password(tokenData.getPassword())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepositoryPort.save(user);

        verificationTokenPort.delete(tokenHash);
    }
}
