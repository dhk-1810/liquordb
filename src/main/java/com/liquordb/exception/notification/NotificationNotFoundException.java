package com.liquordb.exception.notification;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.notice.NoticeException;

import java.util.Map;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(Long notificationId) {
        super(
                ErrorCode.NOTIFICATION_NOT_FOUND,
                "알림을 찾을 수 없습니다.",
                Map.of("notificationId", notificationId)
        );
    }

}