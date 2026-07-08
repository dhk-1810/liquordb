package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class SelfCommentLikeException extends CommentException {

    public SelfCommentLikeException(Long commentId, UUID userId) {
        super(
                ErrorCode.SELF_COMMENT_LIKE_NOT_ALLOWED,
                "본인이 작성한 댓글에는 좋아요를 누를 수 없습니다",
                Map.of(
                        "commentId", commentId,
                        "userId", userId
                )
        );
    }
}
