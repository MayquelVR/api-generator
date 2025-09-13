package com.viewdatatools.apigenarator.auth.service;

import com.viewdatatools.apigenarator.auth.exception.TokenHasExpiredException;
import com.viewdatatools.apigenarator.auth.model.PasswordResetToken;
import com.viewdatatools.apigenarator.auth.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void create(String email, UUID token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);

        passwordResetTokenRepository.save(resetToken);
    }

    public PasswordResetToken validate(UUID token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (passwordResetToken.getExpiration().isBefore(LocalDateTime.now()))
            throw new TokenHasExpiredException("The token has expired");

        passwordResetTokenRepository.delete(passwordResetToken);

        return passwordResetToken;
    }
}
