package com.smartspend.auth.mapper;

import com.smartspend.auth.dto.request.RegisterRequest;
import com.smartspend.auth.dto.response.UserResponse;
import com.smartspend.auth.entity.User;
import com.smartspend.auth.enums.AuthProvider;
import org.mapstruct.Mapper;

import java.util.Locale;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    default User toEntity(
            RegisterRequest request,
            String passwordHash
    ) {
        Objects.requireNonNull(
                request,
                "Register request must not be null"
        );

        Objects.requireNonNull(
                passwordHash,
                "Password hash must not be null"
        );

        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        String normalizedFullName = request.fullName()
                .trim()
                .replaceAll("\\s+", " ");

        return new User(
                normalizedEmail,
                passwordHash,
                normalizedFullName,
                AuthProvider.LOCAL
        );
    }
}