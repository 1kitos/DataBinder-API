package com.databinder.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.databinder.core.dto.LoginResponse;
import com.databinder.core.dto.UserResponse;
import com.databinder.core.dto.request.UserLoginRequest;
import com.databinder.core.dto.request.UserRegisterRequest;
import com.databinder.core.services.AuthService;
import com.databinder.core.services.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {

        UserResponse response = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}
