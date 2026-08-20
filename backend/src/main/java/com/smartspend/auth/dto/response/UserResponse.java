package com.smartspend.auth.dto.response;

import com.smartspend.auth.enums.UserRole;
import com.smartspend.auth.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(

        Long id,

        String email,

        String fullName,

        String avatarUrl,

        UserRole role,

        UserStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}