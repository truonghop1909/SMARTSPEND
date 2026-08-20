package com.smartspend.auth.service;

import com.smartspend.auth.entity.User;

public interface RefreshTokenService {

    IssuedRefreshToken issue(
            User user,
            String deviceName,
            String ipAddress
    );

    RotatedRefreshToken rotate(
            String rawRefreshToken,
            String deviceName,
            String ipAddress
    );

    void revoke(
            String rawRefreshToken,
            Long expectedUserId
    );

    record IssuedRefreshToken(
            String rawToken
    ) {
    }

    record RotatedRefreshToken(
            User user,
            String rawToken
    ) {
    }
}