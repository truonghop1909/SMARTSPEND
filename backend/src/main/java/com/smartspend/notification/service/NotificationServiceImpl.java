package com.smartspend.notification.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
import com.smartspend.notification.dto.response.NotificationResponse;
import com.smartspend.notification.entity.Notification;
import com.smartspend.notification.entity.NotificationType;
import com.smartspend.notification.mapper.NotificationMapper;
import com.smartspend.notification.repository.NotificationRepository;
import com.smartspend.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationMapper notificationMapper
    ) {
        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;

        this.notificationMapper =
                notificationMapper;
    }

    // =========================================================
    // CREATE INTERNAL NOTIFICATION
    // =========================================================

    @Override
    @Transactional
    public NotificationResponse create(
            Long userId,
            NotificationType type,
            String title,
            String content,
            String actionUrl
    ) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        Notification notification =
                notificationMapper.toEntity(
                        user,
                        type,
                        title,
                        content,
                        actionUrl
                );

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        return notificationMapper.toResponse(
                savedNotification
        );
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAll(
            Pageable pageable
    ) {
        Long userId =
                getCurrentUserId();

        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(
                        notificationMapper::toResponse
                );
    }

    // =========================================================
    // UNREAD COUNT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long countUnread() {
        Long userId =
                getCurrentUserId();

        return notificationRepository
                .countByUserIdAndReadFalse(
                        userId
                );
    }

    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @Override
    @Transactional
    public void markAsRead(
            Long notificationId
    ) {
        Long userId =
                getCurrentUserId();

        Notification notification =
                findOwnedNotification(
                        notificationId,
                        userId
                );

        if (notification.isRead()) {
            return;
        }

        notification.markAsRead();

        notificationRepository.save(
                notification
        );
    }

    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId =
                getCurrentUserId();

        notificationRepository
                .markAllAsRead(
                        userId
                );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    @Transactional
    public void delete(
            Long notificationId
    ) {
        Long userId =
                getCurrentUserId();

        Notification notification =
                findOwnedNotification(
                        notificationId,
                        userId
                );

        notificationRepository.delete(
                notification
        );
    }

    // =========================================================
    // OWNERSHIP
    // =========================================================

    private Notification findOwnedNotification(
            Long notificationId,
            Long userId
    ) {
        return notificationRepository
                .findByIdAndUserId(
                        notificationId,
                        userId
                )
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.NOTIFICATION_NOT_FOUND
                        )
                );
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                instanceof UserPrincipal principal)) {

            throw new AppException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        return principal.getId();
    }
}