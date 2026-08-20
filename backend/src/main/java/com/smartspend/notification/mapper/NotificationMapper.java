package com.smartspend.notification.mapper;

import com.smartspend.auth.entity.User;
import com.smartspend.notification.dto.response.NotificationResponse;
import com.smartspend.notification.entity.Notification;
import com.smartspend.notification.entity.NotificationType;
import org.mapstruct.Mapper;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(
            Notification notification
    );

    default Notification toEntity(
            User user,
            NotificationType type,
            String title,
            String content,
            String actionUrl
    ) {
        Objects.requireNonNull(
                user,
                "User must not be null"
        );

        Objects.requireNonNull(
                type,
                "Notification type must not be null"
        );

        Objects.requireNonNull(
                title,
                "Notification title must not be null"
        );

        Objects.requireNonNull(
                content,
                "Notification content must not be null"
        );

        return new Notification(
                user,
                normalizeRequired(title),
                normalizeRequired(content),
                type,
                normalizeNullable(actionUrl)
        );
    }

    private String normalizeRequired(
            String value
    ) {
        return value
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }
}