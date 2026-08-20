package com.smartspend.auth.service;

import com.smartspend.auth.entity.RefreshToken;
import com.smartspend.auth.entity.User;
import com.smartspend.auth.repository.RefreshTokenRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private static final int REFRESH_TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom;
    private final Duration refreshTokenExpiration;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-token-expiration:30d}")
            Duration refreshTokenExpiration
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.secureRandom = new SecureRandom();
    }

    @Override
    @Transactional
    public IssuedRefreshToken issue(
            User user,
            String deviceName,
            String ipAddress
    ) {
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                new RefreshToken(
                        user,
                        tokenHash,
                        normalizeDeviceName(deviceName),
                        normalizeIpAddress(ipAddress),
                        LocalDateTime.now()
                                .plus(refreshTokenExpiration)
                );

        refreshTokenRepository.save(refreshToken);

        return new IssuedRefreshToken(rawToken);
    }

    @Override
    @Transactional
    public RotatedRefreshToken rotate(
            String rawRefreshToken,
            String deviceName,
            String ipAddress
    ) {
        String tokenHash =
                hashToken(rawRefreshToken);

        RefreshToken currentToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.AUTH_REFRESH_TOKEN_INVALID
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        if (!currentToken.isActive(now)) {
            throw new AppException(
                    ErrorCode.AUTH_REFRESH_TOKEN_INVALID
            );
        }

        User user = currentToken.getUser();

        // Token cũ bị thu hồi ngay khi refresh.
        currentToken.revoke(now);

        refreshTokenRepository.save(currentToken);

        IssuedRefreshToken newToken =
                issue(
                        user,
                        deviceName,
                        ipAddress
                );

        return new RotatedRefreshToken(
                user,
                newToken.rawToken()
        );
    }

    @Override
    @Transactional
    public void revoke(
            String rawRefreshToken,
            Long expectedUserId
    ) {
        String tokenHash =
                hashToken(rawRefreshToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.AUTH_REFRESH_TOKEN_INVALID
                                )
                        );

        if (!refreshToken
                .getUser()
                .getId()
                .equals(expectedUserId)) {

            throw new AppException(
                    ErrorCode.AUTH_REFRESH_TOKEN_INVALID
            );
        }

        if (!refreshToken.isRevoked()) {
            refreshToken.revoke(
                    LocalDateTime.now()
            );

            refreshTokenRepository.save(
                    refreshToken
            );
        }
    }

    private String generateRawToken() {
        byte[] randomBytes =
                new byte[REFRESH_TOKEN_BYTES];

        secureRandom.nextBytes(randomBytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash = digest.digest(
                    rawToken.getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }

    private String normalizeDeviceName(
            String deviceName
    ) {
        if (deviceName == null
                || deviceName.isBlank()) {
            return null;
        }

        String normalized =
                deviceName.trim();

        return normalized.length() <= 255
                ? normalized
                : normalized.substring(0, 255);
    }

    private String normalizeIpAddress(
            String ipAddress
    ) {
        if (ipAddress == null
                || ipAddress.isBlank()) {
            return null;
        }

        String normalized =
                ipAddress.trim();

        return normalized.length() <= 45
                ? normalized
                : normalized.substring(0, 45);
    }
}