package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class CommentLikeNotFoundException extends CommentException {

    public CommentLikeNotFoundException(Long commentId) {
        super(
                ErrorCode.COMMENT_LIKE_NOT_FOUND,
                "좋아요 내역을 찾을 수 없습니다.",
                Map.of("commentId", commentId)
        );
    }
}
