package com.liquordb.repository.comment.condition;

import com.liquordb.entity.Comment;

public record CommentSearchCondition(
        String username,
        Comment.CommentStatus commentStatus,
        int page,
        int limit,
        boolean descending
) {
}
