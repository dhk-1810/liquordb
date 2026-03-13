package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class CommentLikeNotFoundException extends CommentException {

    public CommentLikeNotFoundException(Long commentId, UUID userId) {
        super(
                ErrorCode.COMMENT_LIKE_NOT_FOUND,
                "좋아요 내역을 찾을 수 없습니다.",
                Map.of(
                        "commentId", commentId,
                        "userId", userId
                )
        );
    }
}
