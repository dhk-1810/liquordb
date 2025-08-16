package com.liquordb.review.dto.reviewdetaildto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerReviewDetailDto {
    private Double aroma; // 향 평가
    private Double taste; // 맛 평가
    private Double headRetention; // 거품 지속력
    private Double look; // 비주얼 평가
}
