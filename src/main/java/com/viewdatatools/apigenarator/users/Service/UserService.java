package com.viewdatatools.apigenarator.users.Service;

import com.viewdatatools.apigenarator.security.JwtUtil;
import com.viewdatatools.apigenarator.users.dto.LoginReq;
import com.viewdatatools.apigenarator.users.dto.RegisterReq;
import com.viewdatatools.apigenarator.users.dto.LoginResp;
import com.viewdatatools.apigenarator.users.exception.*;
import com.viewdatatools.apigenarator.users.model.User;
import com.viewdatatools.apigenarator.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MailService mailService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final VerificationTokenService verificationTokenService;

    public void register(RegisterReq request) {
        validateUserNotExists(request.getUsername(), request.getEmail());

        UUID token = UUID.randomUUID();

        verificationTokenService.create(request.getUsername(), request.getEmail(), request.getPassword(), token);

        mailService.sendMail(
                request.getEmail(),
                "Activate your account",
                "Welcome " + request.getUsername() + "!\n\nActivate your account by clicking the following link:\nhttp://localhost:8080/activation-account?token=" + token
        );
    }

    public void verify(UUID token) {
        var verificationToken = verificationTokenService.validate(token);

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

        UUID token = UUID.randomUUID();

        passwordResetTokenService.create(email, token);

        mailService.sendMail(
                email,
                "Password recovery",
                "To reset your password, click the link below:\nhttp://localhost:8080/reset-password?token=" + token
        );
    }

    public void resetPassword(UUID token, String newPassword) {
        var passwordResetToken = passwordResetTokenService.validate(token);

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