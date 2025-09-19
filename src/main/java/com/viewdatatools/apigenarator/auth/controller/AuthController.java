package com.viewdatatools.apigenarator.auth.controller;

import com.viewdatatools.apigenarator.auth.service.UserService;
import com.viewdatatools.apigenarator.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyReq request) {
        userService.verify(request.getToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq request) {
        LoginResp resp = userService.login(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordReq request) {
        userService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordReq request) {
        userService.resetPassword(request.getToken(), request.getPassword());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
