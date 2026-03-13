package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class CommentLikeAlreadyExistsException extends CommentException {

    public CommentLikeAlreadyExistsException(Long commentId) {
        super(
                ErrorCode.COMMENT_LIKE_ALREADY_EXISTS,
                "이미 좋아요 한 댓글입니다",
                Map.of("commentId", commentId)
        );
    }

}
