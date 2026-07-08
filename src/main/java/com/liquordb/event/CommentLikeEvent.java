package com.liquordb.event;

import java.util.UUID;

public record CommentLikeEvent (
        Long commentId,
        boolean isLiked,
        String senderUsername,
        UUID receiverId,
        UUID senderId
) {
}
