package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class CommentAccessDeniedException extends CommentException {

    public CommentAccessDeniedException(Long commentId, UUID userId) {
        super(
                ErrorCode.COMMENT_ACCESS_DENIED,
                "댓글 수정/삭제 권한이 없습니다.",
                Map.of("commentId", commentId, "userId", userId)
        );
    }

}
