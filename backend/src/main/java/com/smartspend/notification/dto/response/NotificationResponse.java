package com.smartspend.notification.dto.response;

import com.smartspend.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(

        Long id,

        String title,

        String content,

        NotificationType type,

        String actionUrl,

        boolean read,

        LocalDateTime createdAt

) {
}