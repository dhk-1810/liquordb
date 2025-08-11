package com.liquordb.review.dto.detaildto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WineReviewDetailDto {
    private Double sweetness;       // 당도
    private Double acidity;       // 산도
    private Double body;       // 바디
    private Double tannin;      // 타닌감 (떫은맛)
}
