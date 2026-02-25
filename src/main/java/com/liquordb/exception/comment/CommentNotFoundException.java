package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class CommentNotFoundException extends CommentException {

    public CommentNotFoundException(Long commentId) {
        super(
                ErrorCode.COMMENT_NOT_FOUND,
                "댓글을 찾을 수 없습니다.",
                Map.of("commentId", commentId)
        );
    }

}
