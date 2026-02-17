package com.liquordb.entity.reviewdetail;

import com.liquordb.entity.Review;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@DiscriminatorValue("WINE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WineReviewDetail extends ReviewDetail {
    private Double sweetness;       // 당도
    private Double acidity;       // 산도
    private Double body;       // 바디
    private Double tannin;      // 타닌감 (떫은맛)

    @OneToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public void update(Double sweetness, Double acidity, Double body, Double tannin) {
        this.sweetness = sweetness;
        this.acidity = acidity;
        this.body = body;
        this.tannin = tannin;
    }
}
