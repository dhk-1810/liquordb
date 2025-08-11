package com.liquordb.review.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("BEER")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerReviewDetail extends ReviewDetail {
    private Double aroma; // 향 평가
    private Double taste; // 맛 평가
    private Double headRetention; // 거품 지속력
    private Double look; // 비주얼 평가
}