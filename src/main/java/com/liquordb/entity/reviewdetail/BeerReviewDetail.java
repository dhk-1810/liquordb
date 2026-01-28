package com.liquordb.entity.reviewdetail;

import com.liquordb.entity.Review;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@DiscriminatorValue("BEER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerReviewDetail extends ReviewDetail {
    private Double aroma; // 향 평가
    private Double taste; // 맛 평가
    private Double headRetention; // 거품 지속력
    private Double look; // 비주얼 평가

    @OneToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public void update(Double aroma, Double taste, Double headRetention, Double look) {
        this.aroma = aroma;
        this.taste = taste;
        this.headRetention = headRetention;
        this.look = look;
    }
}