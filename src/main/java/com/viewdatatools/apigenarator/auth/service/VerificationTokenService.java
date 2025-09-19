package com.viewdatatools.apigenarator.auth.service;

import com.viewdatatools.apigenarator.auth.exception.InvalidTokenException;
import com.viewdatatools.apigenarator.auth.exception.TokenHasExpiredException;
import com.viewdatatools.apigenarator.auth.model.VerificationToken;
import com.viewdatatools.apigenarator.auth.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void create(String username, String email, String password, String tokenHash) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUsername(username);
        verificationToken.setEmail(email);
        verificationToken.setPassword(passwordEncoder.encode(password));
        verificationToken.setTokenHash(tokenHash);

        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken validate(String tokenHash) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenHasExpiredException("The token has expired");
        }

        verificationTokenRepository.delete(verificationToken);

        return verificationToken;
    }
}
