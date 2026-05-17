package com.liquordb.exception.notification;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationAccessDeniedException extends NotificationException {
    public NotificationAccessDeniedException(Long id, UUID userId) {
        super(
                ErrorCode.NOTIFICATION_ACCESS_DENIED,
                "알림 접근 권한이 없습니다",
                Map.of("notificationId", id, "userId", userId.toString())
        );
    }
}
