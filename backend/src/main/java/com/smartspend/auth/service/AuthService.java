package com.smartspend.auth.service;

import com.smartspend.auth.dto.request.LoginRequest;
import com.smartspend.auth.dto.request.LogoutRequest;
import com.smartspend.auth.dto.request.RefreshTokenRequest;
import com.smartspend.auth.dto.request.RegisterRequest;
import com.smartspend.auth.dto.response.AuthResponse;
import com.smartspend.auth.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(
            LoginRequest request,
            String ipAddress,
            String userAgent
    );

    AuthResponse refresh(
            RefreshTokenRequest request,
            String ipAddress,
            String userAgent
    );

    void logout(LogoutRequest request);

    UserResponse getCurrentUser();
}