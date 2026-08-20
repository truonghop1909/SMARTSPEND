package com.smartspend.notification.repository;

import com.smartspend.notification.entity.Notification;
import com.smartspend.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Optional<Notification> findByIdAndUserId(
            Long notificationId,
            Long userId
    );

    long countByUserIdAndReadFalse(
            Long userId
    );

    boolean existsByUserIdAndTypeAndActionUrl(
            Long userId,
            NotificationType type,
            String actionUrl
    );

    @Modifying
    @Query("""
            update Notification n
            set n.read = true
            where n.user.id = :userId
              and n.read = false
            """)
    int markAllAsRead(
            @Param("userId") Long userId
    );
}