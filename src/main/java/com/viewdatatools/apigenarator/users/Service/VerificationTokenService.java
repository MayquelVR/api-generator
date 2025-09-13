package com.viewdatatools.apigenarator.users.Service;

import com.viewdatatools.apigenarator.users.exception.InvalidTokenException;
import com.viewdatatools.apigenarator.users.exception.TokenHasExpiredException;
import com.viewdatatools.apigenarator.users.model.VerificationToken;
import com.viewdatatools.apigenarator.users.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void create(String username, String email, String password, UUID token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUsername(username);
        verificationToken.setEmail(email);
        verificationToken.setPassword(passwordEncoder.encode(password));
        verificationToken.setToken(token);

        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken validate(UUID token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenHasExpiredException("The token has expired");
        }

        verificationTokenRepository.delete(verificationToken);

        return verificationToken;
    }
}
