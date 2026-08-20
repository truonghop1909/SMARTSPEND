package com.smartspend.notification.controller;

import com.smartspend.notification.dto.response.NotificationResponse;
import com.smartspend.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService =
                notificationService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getAll(
            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            int size
    ) {
        int resolvedPage =
                Math.max(page, 0);

        int resolvedSize =
                Math.min(
                        Math.max(
                                size,
                                1
                        ),
                        MAX_PAGE_SIZE
                );

        Pageable pageable =
                PageRequest.of(
                        resolvedPage,
                        resolvedSize,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        return ResponseEntity.ok(
                notificationService.getAll(
                        pageable
                )
        );
    }

    // =========================================================
    // UNREAD COUNT
    // =========================================================

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>>
    countUnread() {

        long unreadCount =
                notificationService
                        .countUnread();

        return ResponseEntity.ok(
                Map.of(
                        "unreadCount",
                        unreadCount
                )
        );
    }

    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(
                notificationId
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long notificationId
    ) {
        notificationService.delete(
                notificationId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}