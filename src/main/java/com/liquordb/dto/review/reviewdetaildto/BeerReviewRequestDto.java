package com.liquordb.dto.review.reviewdetaildto;

import lombok.Builder;

@Builder
public record BeerReviewRequestDto (
    Double aroma, // 향 평가
    Double taste, // 맛 평가
    Double headRetention, // 거품 지속력
    Double look // 비주얼 평가
) implements ReviewDetailRequest { }
