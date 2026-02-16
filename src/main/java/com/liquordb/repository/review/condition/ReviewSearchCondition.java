package com.liquordb.repository.review.condition;

import com.liquordb.entity.Review;

public record ReviewSearchCondition(
        String username,
        Review.ReviewStatus reviewStatus,
        int page,
        int limit,
        boolean descending
) {
}
