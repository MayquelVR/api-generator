package com.viewdatatools.apigenarator.users.Service;

import com.viewdatatools.apigenarator.security.JwtUtil;
import com.viewdatatools.apigenarator.users.dto.UserLogin;
import com.viewdatatools.apigenarator.users.dto.UserRegister;
import com.viewdatatools.apigenarator.users.dto.UserResponse;
import com.viewdatatools.apigenarator.users.exception.InvalidCredentialsException;
import com.viewdatatools.apigenarator.users.exception.UserNotFoundException;
import com.viewdatatools.apigenarator.users.exception.UsernameAlreadyExistsException;
import com.viewdatatools.apigenarator.users.model.User;
import com.viewdatatools.apigenarator.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Registrar usuario
    public UserResponse register(UserRegister request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameAlreadyExistsException("The email is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    public UserResponse login(UserLogin request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }
}