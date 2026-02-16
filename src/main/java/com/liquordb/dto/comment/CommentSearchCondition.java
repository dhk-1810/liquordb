package com.liquordb.dto.comment;

import com.liquordb.entity.Comment;

public record CommentSearchCondition(
        String username,
        Comment.CommentStatus commentStatus,
        int page,
        int limit,
        boolean descending
) {
}
