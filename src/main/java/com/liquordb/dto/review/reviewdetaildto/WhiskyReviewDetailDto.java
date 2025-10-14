package com.liquordb.dto.review.reviewdetaildto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhiskyReviewDetailDto {
    private Double aroma;    // 향
    private Double taste;    // 맛
    private Double finish;   // 여운
    private Double body;     // 바디감
}
