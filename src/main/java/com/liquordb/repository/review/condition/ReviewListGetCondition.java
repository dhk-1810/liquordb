package com.liquordb.repository.review.condition;

import com.liquordb.entity.Review;
import com.liquordb.enums.ReviewSortBy;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewListGetCondition(
        Long liquorId,
        UUID userId,
        Review.ReviewStatus status,
        Short rating,
        Long cursor,
        Long idAfter,
        int limit,
        ReviewSortBy sortBy,
        boolean descending
) {
}
