package com.smartspend.auth.service;

import com.smartspend.auth.dto.request.RegisterRequest;
import com.smartspend.auth.dto.response.UserResponse;
import com.smartspend.auth.entity.User;
import com.smartspend.auth.enums.AuthProvider;
import com.smartspend.auth.enums.UserRole;
import com.smartspend.auth.enums.UserStatus;
import com.smartspend.auth.mapper.UserMapper;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.auth.repository.UserSessionRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                userSessionRepository,
                userMapper,
                passwordEncoder,
                authenticationManager,
                jwtService,
                refreshTokenService,
                redisTemplate,
                5,
                Duration.ofMinutes(1)
        );
    }

    @Test
    void register_shouldCreateUser_whenEmailDoesNotExist() {
        RegisterRequest request =
                new RegisterRequest(
                        "User@Example.com",
                        "Password@123",
                        "Nguyen Van A"
                );

        String passwordHash =
                "$2a$12$hashed-password";

        User user =
                new User(
                        "user@example.com",
                        passwordHash,
                        "Nguyen Van A",
                        AuthProvider.LOCAL
                );

        UserResponse expectedResponse =
                new UserResponse(
                        1L,
                        "user@example.com",
                        "Nguyen Van A",
                        null,
                        UserRole.USER,
                        UserStatus.ACTIVE,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "user@example.com"
                )
        ).thenReturn(false);

        when(
                passwordEncoder.encode(
                        "Password@123"
                )
        ).thenReturn(passwordHash);

        when(
                userMapper.toEntity(
                        request,
                        passwordHash
                )
        ).thenReturn(user);

        when(
                userRepository.save(user)
        ).thenReturn(user);

        when(
                userMapper.toResponse(user)
        ).thenReturn(expectedResponse);

        UserResponse result =
                authService.register(request);

        assertNotNull(result);
        assertEquals(
                "user@example.com",
                result.email()
        );

        assertEquals(
                "Nguyen Van A",
                result.fullName()
        );

        verify(
                passwordEncoder
        ).encode("Password@123");

        verify(
                userRepository
        ).save(user);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        RegisterRequest request =
                new RegisterRequest(
                        "user@example.com",
                        "Password@123",
                        "Nguyen Van A"
                );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "user@example.com"
                )
        ).thenReturn(true);

        assertThrows(
                AppException.class,
                () -> authService.register(request)
        );

        verify(
                passwordEncoder,
                never()
        ).encode(anyString());

        verify(
                userRepository,
                never()
        ).save(any());
    }
}