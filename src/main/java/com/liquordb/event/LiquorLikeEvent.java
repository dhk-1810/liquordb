package com.liquordb.event;

public record LiquorLikeEvent (
        Long liquorId,
        boolean isLiked
) {
}
