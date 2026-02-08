package com.liquordb.event;

public record ReviewLikeEvent (
        Long reviewId,
        boolean isLiked
) {
}
