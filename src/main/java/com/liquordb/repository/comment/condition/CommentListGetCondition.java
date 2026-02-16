package com.liquordb.repository.comment.condition;

import com.liquordb.entity.Comment;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentListGetCondition(
        Long reviewId,
        UUID userId,
        Comment.CommentStatus status,
        Long cursor,
        Long idAfter,
        int limit,
        boolean useId, // 정렬 기준 (ID / 좋아요 수)
        boolean descending
) {
}
