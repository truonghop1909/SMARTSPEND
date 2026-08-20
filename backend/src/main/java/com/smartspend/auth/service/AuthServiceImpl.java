package com.smartspend.auth.service;

import com.smartspend.auth.dto.request.LoginRequest;
import com.smartspend.auth.dto.request.LogoutRequest;
import com.smartspend.auth.dto.request.RefreshTokenRequest;
import com.smartspend.auth.dto.request.RegisterRequest;
import com.smartspend.auth.dto.response.AuthResponse;
import com.smartspend.auth.dto.response.UserResponse;
import com.smartspend.auth.entity.User;
import com.smartspend.auth.entity.UserSession;
import com.smartspend.auth.mapper.UserMapper;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.auth.repository.UserSessionRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
import com.smartspend.security.JwtService;
import com.smartspend.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AuthServiceImpl.class
            );

    private static final String LOGIN_ATTEMPT_PREFIX =
            "auth:login-attempt:";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, Object> redisTemplate;

    private final int maxLoginAttempts;
    private final Duration loginAttemptWindow;

    public AuthServiceImpl(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            RedisTemplate<String, Object> redisTemplate,

            @Value("${app.security.login.max-attempts:5}")
            int maxLoginAttempts,

            @Value("${app.security.login.window:1m}")
            Duration loginAttemptWindow
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository =
                userSessionRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager =
                authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService =
                refreshTokenService;
        this.redisTemplate = redisTemplate;
        this.maxLoginAttempts =
                maxLoginAttempts;
        this.loginAttemptWindow =
                loginAttemptWindow;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    @Transactional
    public UserResponse register(
            RegisterRequest request
    ) {
        String email =
                normalizeEmail(request.email());

        if (userRepository
                .existsByEmailIgnoreCase(email)) {

            throw new AppException(
                    ErrorCode.AUTH_EMAIL_ALREADY_EXISTS
            );
        }

        String passwordHash =
                passwordEncoder.encode(
                        request.password()
                );

        User user =
                userMapper.toEntity(
                        request,
                        passwordHash
                );

        User savedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                savedUser
        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    @Transactional
    public AuthResponse login(
            LoginRequest request,
            String ipAddress,
            String userAgent
    ) {
        String email =
                normalizeEmail(request.email());

        checkLoginRateLimit(email);

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.password()
                            )
                    );

            UserPrincipal principal =
                    (UserPrincipal)
                            authentication.getPrincipal();

            User user =
                    userRepository
                            .findById(
                                    principal.getId()
                            )
                            .orElseThrow(() ->
                                    new AppException(
                                            ErrorCode.USER_NOT_FOUND
                                    )
                            );

            String accessToken =
                    jwtService.generateAccessToken(
                            principal
                    );

            RefreshTokenService
                    .IssuedRefreshToken refreshToken =
                    refreshTokenService.issue(
                            user,
                            userAgent,
                            ipAddress
                    );

            saveUserSession(
                    user,
                    userAgent,
                    ipAddress
            );

            clearLoginAttempts(email);

            return AuthResponse.of(
                    accessToken,
                    refreshToken.rawToken(),
                    jwtService
                            .getAccessTokenExpirationSeconds(),
                    userMapper.toResponse(user)
            );

        } catch (AuthenticationException exception) {
            increaseLoginAttempts(email);

            throw new AppException(
                    ErrorCode.AUTH_INVALID_CREDENTIALS
            );
        }
    }

    // =========================================================
    // REFRESH TOKEN ROTATION
    // =========================================================

    @Override
    @Transactional
    public AuthResponse refresh(
            RefreshTokenRequest request,
            String ipAddress,
            String userAgent
    ) {
        RefreshTokenService
                .RotatedRefreshToken rotatedToken =
                refreshTokenService.rotate(
                        request.refreshToken(),
                        userAgent,
                        ipAddress
                );

        User user =
                rotatedToken.user();

        UserPrincipal principal =
                UserPrincipal.from(user);

        String newAccessToken =
                jwtService.generateAccessToken(
                        principal
                );

        return AuthResponse.of(
                newAccessToken,
                rotatedToken.rawToken(),
                jwtService
                        .getAccessTokenExpirationSeconds(),
                userMapper.toResponse(user)
        );
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @Override
    @Transactional
    public void logout(
            LogoutRequest request
    ) {
        UserPrincipal principal =
                getAuthenticatedPrincipal();

        refreshTokenService.revoke(
                request.refreshToken(),
                principal.getId()
        );
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UserPrincipal principal =
                getAuthenticatedPrincipal();

        User user =
                userRepository
                        .findById(
                                principal.getId()
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        return userMapper.toResponse(user);
    }

    // =========================================================
    // USER SESSION
    // =========================================================

    private void saveUserSession(
            User user,
            String userAgent,
            String ipAddress
    ) {
        UserSession session =
                new UserSession(
                        user,
                        normalizeDeviceName(userAgent),
                        null,
                        null,
                        null,
                        normalizeIpAddress(ipAddress),
                        LocalDateTime.now()
                );

        userSessionRepository.save(
                session
        );
    }

    // =========================================================
    // REDIS LOGIN RATE LIMIT
    // =========================================================

    private void checkLoginRateLimit(
            String email
    ) {
        try {
            String key =
                    loginAttemptKey(email);

            Object storedValue =
                    redisTemplate
                            .opsForValue()
                            .get(key);

            long attempts =
                    toLong(storedValue);

            if (attempts
                    >= maxLoginAttempts) {

                throw new AppException(
                        ErrorCode.AUTH_RATE_LIMIT_EXCEEDED
                );
            }

        } catch (AppException exception) {
            throw exception;

        } catch (DataAccessException exception) {
            /*
             * Redis là lớp bảo vệ bổ sung.
             * Nếu Redis tạm thời lỗi, không làm toàn bộ
             * chức năng đăng nhập ngừng hoạt động.
             */
            log.warn(
                    "Redis unavailable while checking login rate limit"
            );
        }
    }

    private void increaseLoginAttempts(
            String email
    ) {
        try {
            String key =
                    loginAttemptKey(email);

            Long attempts =
                    redisTemplate
                            .opsForValue()
                            .increment(key);

            if (attempts != null
                    && attempts == 1L) {

                redisTemplate.expire(
                        key,
                        loginAttemptWindow
                                .toSeconds(),
                        TimeUnit.SECONDS
                );
            }

        } catch (DataAccessException exception) {
            log.warn(
                    "Redis unavailable while increasing login attempts"
            );
        }
    }

    private void clearLoginAttempts(
            String email
    ) {
        try {
            redisTemplate.delete(
                    loginAttemptKey(email)
            );

        } catch (DataAccessException exception) {
            log.warn(
                    "Redis unavailable while clearing login attempts"
            );
        }
    }

    private String loginAttemptKey(
            String email
    ) {
        return LOGIN_ATTEMPT_PREFIX
                + email;
    }

    private long toLong(
            Object value
    ) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.parseLong(
                    value.toString()
            );
        } catch (NumberFormatException exception) {
            return 0L;
        }
    }

    // =========================================================
    // SECURITY CONTEXT
    // =========================================================

    private UserPrincipal getAuthenticatedPrincipal() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                instanceof UserPrincipal principal)) {

            throw new AppException(
                    ErrorCode.AUTH_INVALID_CREDENTIALS
            );
        }

        return principal;
    }

    // =========================================================
    // NORMALIZATION
    // =========================================================

    private String normalizeEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
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
                : normalized.substring(
                        0,
                        255
                );
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
                : normalized.substring(
                        0,
                        45
                );
    }
}