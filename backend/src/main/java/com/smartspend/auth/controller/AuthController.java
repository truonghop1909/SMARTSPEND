package com.smartspend.auth.controller;

import com.smartspend.auth.dto.request.LoginRequest;
import com.smartspend.auth.dto.request.LogoutRequest;
import com.smartspend.auth.dto.request.RefreshTokenRequest;
import com.smartspend.auth.dto.request.RegisterRequest;
import com.smartspend.auth.dto.response.AuthResponse;
import com.smartspend.auth.dto.response.UserResponse;
import com.smartspend.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String UNKNOWN = "unknown";

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        AuthResponse response =
                authService.login(
                        request,
                        resolveIpAddress(httpRequest),
                        resolveUserAgent(httpRequest)
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // REFRESH TOKEN
    // =========================

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        AuthResponse response =
                authService.refresh(
                        request,
                        resolveIpAddress(httpRequest),
                        resolveUserAgent(httpRequest)
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // LOGOUT
    // =========================

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(request);

        return ResponseEntity.noContent().build();
    }

    // =========================
    // CURRENT USER
    // =========================

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response =
                authService.getCurrentUser();

        return ResponseEntity.ok(response);
    }

    // =========================
    // REQUEST INFORMATION
    // =========================

    private String resolveIpAddress(
            HttpServletRequest request
    ) {
        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        String remoteAddress =
                request.getRemoteAddr();

        return remoteAddress != null
                ? remoteAddress
                : UNKNOWN;
    }

    private String resolveUserAgent(
            HttpServletRequest request
    ) {
        String userAgent =
                request.getHeader(
                        HttpHeaders.USER_AGENT
                );

        return userAgent != null
                && !userAgent.isBlank()
                ? userAgent
                : UNKNOWN;
    }
}