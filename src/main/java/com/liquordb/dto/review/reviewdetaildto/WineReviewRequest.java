package com.liquordb.dto.review.reviewdetaildto;

import lombok.*;

@Builder
public record WineReviewRequest(
        Double sweetness,       // 당도
        Double acidity,       // 산도
        Double body,       // 바디
        Double tannin      // 타닌감 (떫은맛)
) implements ReviewDetailRequest {

}
