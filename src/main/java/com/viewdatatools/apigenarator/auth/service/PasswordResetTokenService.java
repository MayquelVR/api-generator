package com.viewdatatools.apigenarator.auth.service;

import com.viewdatatools.apigenarator.auth.exception.InvalidTokenException;
import com.viewdatatools.apigenarator.auth.exception.TokenHasExpiredException;
import com.viewdatatools.apigenarator.auth.model.PasswordResetToken;
import com.viewdatatools.apigenarator.auth.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void create(String email, String tokenHash) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setTokenHash(tokenHash);

        passwordResetTokenRepository.save(resetToken);
    }

    public PasswordResetToken validate(String tokenHash) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (passwordResetToken.getExpiration().isBefore(LocalDateTime.now()))
            throw new TokenHasExpiredException("The token has expired");

        passwordResetTokenRepository.delete(passwordResetToken);

        return passwordResetToken;
    }
}
