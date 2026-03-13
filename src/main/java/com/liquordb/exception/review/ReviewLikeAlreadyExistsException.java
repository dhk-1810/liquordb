package com.liquordb.exception.review;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReviewLikeAlreadyExistsException extends ReviewException {

    public ReviewLikeAlreadyExistsException(Long reviewId, UUID userId) {
        super(
                ErrorCode.REVIEW_LIKE_ALREADY_EXISTS,
                "이미 좋아요 한 리뷰입니다",
                Map.of(
                        "reviewId", reviewId,
                        "userId", userId
                )
        );
    }
}
