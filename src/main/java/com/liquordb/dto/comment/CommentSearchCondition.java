package com.liquordb.dto.comment;

import com.liquordb.entity.Comment;
import com.liquordb.enums.CommentSortBy;
import com.liquordb.enums.SortDirection;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentSearchCondition(
        Long reviewId,
        UUID userId,
        Comment.CommentStatus commentStatus,
        Long cursor,
        Long idAfter,
        int limit,
        boolean useId, // 정렬 기준 (ID / 좋아요 수)
        boolean descending
) {
}
