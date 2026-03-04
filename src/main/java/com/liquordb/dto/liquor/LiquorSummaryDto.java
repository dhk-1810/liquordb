package com.liquordb.dto.liquor;

import lombok.*;

/**
 * 다건 조회시 표시할 요약 정보
 */
@Builder
public record LiquorSummaryDto (
        Long id,
        String name,
        String presignedUrl,
        Double averageRating,
        long reviewCount,
        long likeCount,
        boolean likedByMe
) {

}
