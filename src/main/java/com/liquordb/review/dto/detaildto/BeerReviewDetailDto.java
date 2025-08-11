package com.liquordb.review.dto.detaildto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerReviewDetailDto implements ReviewDetailDto {
    private Double aroma; // 향 평가
    private Double taste; // 맛 평가
    private Double headRetention; // 거품 지속력
    private Double look; // 비주얼 평가
}
