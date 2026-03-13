package com.liquordb.exception.review;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class ReviewLikeNotFoundException extends ReviewException {

    public ReviewLikeNotFoundException(Long reviewId) {
        super(
                ErrorCode.REVIEW_LIKE_NOT_FOUND,
                "좋아요 내역을 찾을 수 없습니다.",
                Map.of("reviewId", reviewId)
        );
    }
}
