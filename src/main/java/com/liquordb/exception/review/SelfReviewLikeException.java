package com.liquordb.exception.review;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class SelfReviewLikeException extends ReviewException {

    public SelfReviewLikeException(Long reviewId, UUID userId) {
        super(
                ErrorCode.SELF_REVIEW_LIKE_NOT_ALLOWED,
                "본인이 작성한 리뷰에는 좋아요를 누를 수 없습니다",
                Map.of(
                        "reviewId", reviewId,
                        "userId", userId
                )
        );
    }
}
