package com.liquordb.dto.review.reviewdetaildto;

import lombok.Builder;

@Builder
public record WhiskyReviewRequestDto (
        Double aroma,    // 향
        Double taste,    // 맛
        Double finish,   // 여운
        Double body      // 바디감
) implements ReviewDetailRequest {}
