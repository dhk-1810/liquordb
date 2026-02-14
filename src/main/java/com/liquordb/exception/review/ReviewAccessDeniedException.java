package com.liquordb.exception.review;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;
import java.util.UUID;

public class ReviewAccessDeniedException extends ReviewException {

    public ReviewAccessDeniedException(Long reviewId, UUID userId) {
        super(
                ErrorCode.REVIEW_ACCESS_DENIED,
                "리뷰 수정/삭제 권한이 없습니다.",
                Map.of("reviewId", reviewId, "userId", userId)
        );
    }

}
