package com.hrm.controller;

import com.hrm.dto.AuthDtos.LoginRequest;
import com.hrm.dto.AuthDtos.LoginResponse;
import com.hrm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
