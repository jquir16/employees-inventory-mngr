package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.dto.auth.LoginUserRequest;
import com.katabdb.employee.onboarding.mngr.dto.auth.TokenResponse;
import com.katabdb.employee.onboarding.mngr.dto.auth.RegisterUserRequest;
import com.katabdb.employee.onboarding.mngr.services.implementation.AuthService;
import com.katabdb.employee.onboarding.mngr.services.interfaces.IAuthQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthQueryService authQueryService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@Validated @RequestBody final RegisterUserRequest request) {
        final TokenResponse token = authQueryService.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@Validated @RequestBody final LoginUserRequest request) {
        final TokenResponse token = authQueryService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        final TokenResponse token = authQueryService.refreshToken(authHeader);
        return ResponseEntity.ok(token);
    }
}
