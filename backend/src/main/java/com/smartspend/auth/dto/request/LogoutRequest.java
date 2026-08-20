package com.smartspend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LogoutRequest(

        @NotBlank(message = "Refresh token is required")
        @Size(
                max = 1000,
                message = "Refresh token must not exceed 1000 characters"
        )
        String refreshToken

) {
}