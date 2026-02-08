package com.liquordb.event;

public record CommentLikeEvent (
        Long commentId,
        boolean isLiked
) {
}
