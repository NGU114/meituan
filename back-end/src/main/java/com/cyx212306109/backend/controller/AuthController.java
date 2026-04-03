package com.cyx212306109.backend.controller;

import com.cyx212306109.backend.auth.RequireRole;
import com.cyx212306109.backend.common.ApiResponse;
import com.cyx212306109.backend.dto.AuthDto;
import com.cyx212306109.backend.enums.RoleType;
import com.cyx212306109.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ApiResponse.ok("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ApiResponse.ok("登录成功", authService.login(request));
    }

    @RequireRole({RoleType.USER, RoleType.MERCHANT, RoleType.RIDER, RoleType.ADMIN})
    @GetMapping("/me")
    public ApiResponse<AuthDto.ProfileResponse> me() {
        return ApiResponse.ok(authService.currentProfile());
    }
}
