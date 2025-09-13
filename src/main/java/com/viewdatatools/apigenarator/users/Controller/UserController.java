package com.viewdatatools.apigenarator.users.Controller;

import com.viewdatatools.apigenarator.users.Service.UserService;
import com.viewdatatools.apigenarator.users.dto.UserLogin;
import com.viewdatatools.apigenarator.users.dto.UserRegister;
import com.viewdatatools.apigenarator.users.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRegister request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody UserLogin request) {
        return userService.login(request);
    }
}
