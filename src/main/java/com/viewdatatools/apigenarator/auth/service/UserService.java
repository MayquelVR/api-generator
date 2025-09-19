package com.viewdatatools.apigenarator.auth.service;

import com.viewdatatools.apigenarator.security.JwtUtil;
import com.viewdatatools.apigenarator.auth.dto.LoginReq;
import com.viewdatatools.apigenarator.auth.dto.RegisterReq;
import com.viewdatatools.apigenarator.auth.dto.LoginResp;
import com.viewdatatools.apigenarator.auth.exception.*;
import com.viewdatatools.apigenarator.auth.model.User;
import com.viewdatatools.apigenarator.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MailService mailService;
    private final TokenService tokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final VerificationTokenService verificationTokenService;

    @Value("${app.base-url:http://localhost:4200/activate-account}")
    private String activateAccountBaseUrl;

    @Value("${app.base-url:http://localhost:4200/reset-password}")
    private String resetPasswordBaseUrl;

    public void register(RegisterReq request) {
        validateUserNotExists(request.getUsername(), request.getEmail());

        String token = tokenService.generateToken();
        String tokenHash = tokenService.hash(token);

        verificationTokenService.create(request.getUsername(), request.getEmail(), request.getPassword(), tokenHash);

        mailService.sendMail(
                request.getEmail(),
                "Activate your account",
                "Welcome " + request.getUsername() + "!\n\nActivate your account by clicking the following link:\n" + activateAccountBaseUrl + "?token=" + token
        );
    }

    public void verify(String token) {
        var verificationToken = verificationTokenService.validate(tokenService.hash(token));

        validateUserNotExists(verificationToken.getUsername(), verificationToken.getEmail());

        User user = new User();
        user.setUsername(verificationToken.getUsername());
        user.setEmail(verificationToken.getEmail());
        user.setPassword(verificationToken.getPassword());
        userRepository.save(user);
    }

    public LoginResp login(LoginReq request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        return LoginResp.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    public void createPasswordResetToken(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));

        String token = tokenService.generateToken();
        String tokenHash = tokenService.hash(token);


        passwordResetTokenService.create(email, tokenHash);

        mailService.sendMail(
                email,
                "Password recovery",
                "To reset your password, click the link below:\n" + resetPasswordBaseUrl + "?token=" + token
        );
    }

    public void resetPassword(String token, String newPassword) {
        var passwordResetToken = passwordResetTokenService.validate(tokenService.hash(token));

        User user = userRepository.findByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void validateUserNotExists(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("The username is already registered");
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("The email is already registered");
        }
    }
}