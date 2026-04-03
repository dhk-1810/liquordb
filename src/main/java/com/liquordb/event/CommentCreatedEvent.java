package com.liquordb.event;

import java.util.UUID;

public record CommentCreatedEvent (
        UUID receiverId,
        Long commentId,
        Long liquorId, // 주류 인기 점수 계산에 사용
        String writerUsername
) {
}
