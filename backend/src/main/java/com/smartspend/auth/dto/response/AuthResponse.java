package com.smartspend.auth.dto.response;

public record AuthResponse(

        String tokenType,

        String accessToken,

        String refreshToken,

        long expiresIn,

        UserResponse user

) {

    public static AuthResponse of(
            String accessToken,
            String refreshToken,
            long expiresIn,
            UserResponse user
    ) {
        return new AuthResponse(
                "Bearer",
                accessToken,
                refreshToken,
                expiresIn,
                user
        );
    }

    public static AuthResponse tokenOnly(
            String accessToken,
            String refreshToken,
            long expiresIn
    ) {
        return new AuthResponse(
                "Bearer",
                accessToken,
                refreshToken,
                expiresIn,
                null
        );
    }
}