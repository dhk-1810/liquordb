package com.liquordb.dto.review.reviewdetaildto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WineReviewDetailDto {
    private Double sweetness;       // 당도
    private Double acidity;       // 산도
    private Double body;       // 바디
    private Double tannin;      // 타닌감 (떫은맛)
}
