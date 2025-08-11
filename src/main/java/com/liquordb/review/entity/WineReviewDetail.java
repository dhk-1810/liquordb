package com.liquordb.review.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("WINE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WineReviewDetail extends ReviewDetail {
    private Double sweetness;       // 당도
    private Double acidity;       // 산도
    private Double body;       // 바디
    private Double tannin;      // 타닌감 (떫은맛)
}
