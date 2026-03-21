package com.liquordb.event;

import java.util.UUID;

public record ReviewLikeEvent (
        Long reviewId,
        boolean isLiked,
        String username,
        UUID receiverId
) {
}
