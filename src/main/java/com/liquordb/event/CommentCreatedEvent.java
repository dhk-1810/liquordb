package com.liquordb.event;

import java.util.UUID;

public record CommentCreatedEvent (
        UUID receiverId,
        Long commentId,
        String writerUsername
) {
}
