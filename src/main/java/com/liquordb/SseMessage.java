package com.liquordb;

import java.time.Instant;
import java.util.UUID;

public record SseMessage (
        UUID id,
        UUID receiverId,
        String eventName,   // 이벤트의 종류
        Object data,        // 전송할 실제 객체
        Instant createdAt
){

    public static SseMessage create(UUID receiverId, String eventName, Object data) {
        return new SseMessage(
                UUID.randomUUID(),
                receiverId,
                eventName,
                data,
                Instant.now()
        );
    }
}
