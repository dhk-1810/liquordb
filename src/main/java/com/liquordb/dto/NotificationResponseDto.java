package com.liquordb.dto;

import com.liquordb.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDto (
        Long id,
        UUID receiverId,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static NotificationResponseDto toDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getReceiverId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getCreatedAt()
        );
    }
}
