package com.liquordb.repository.review.condition;

import com.liquordb.entity.Review;
import com.liquordb.enums.SortReviewBy;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewListGetCondition(
        Long liquorId,
        UUID userId,
        Review.ReviewStatus status,
        Integer rating,
        Long cursor,
        Long idAfter,
        int limit,
        SortReviewBy sortBy,
        boolean descending
) {
}
