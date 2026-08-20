package com.smartspend.notification.service;

import com.smartspend.notification.dto.response.NotificationResponse;
import com.smartspend.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse create(
            Long userId,
            NotificationType type,
            String title,
            String content,
            String actionUrl
    );

    Page<NotificationResponse> getAll(
            Pageable pageable
    );

    long countUnread();

    void markAsRead(
            Long notificationId
    );

    void markAllAsRead();

    void delete(
            Long notificationId
    );
}