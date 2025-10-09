package com.viewdatatools.apigenarator.auth.adapter.out.persistence;

import com.viewdatatools.apigenarator.auth.domain.port.out.JwtServicePort;
import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtServiceAdapter implements JwtServicePort {

    private final JwtUtil jwtUtil;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public String generateToken(String username) {
        String email = userRepositoryPort.findByUsername(username)
                .map(user -> user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(username, email);
    }

    @Override
    public String generateRefreshToken(String username) {
        String email = userRepositoryPort.findByUsername(username)
                .map(user -> user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateRefreshToken(username, email);
    }
}
