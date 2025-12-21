package com.liquordb.dto.liquor;

import lombok.*;

@Builder
public record LiquorSummaryDto (
        Long id,
        String name,
        String imageUrl,
        Double averageRating,
        long reviewCount,
        long likeCount,
        boolean likedByMe
) {

}
