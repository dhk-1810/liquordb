package com.liquordb.entity.reviewdetail;

import com.liquordb.entity.Review;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@DiscriminatorValue("WHISKY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WhiskyReviewDetail extends ReviewDetail  {
    private Double aroma;    // 향
    private Double taste;    // 맛
    private Double finish;   // 여운
    private Double body;     // 바디감

    @OneToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public void update(Double aroma, Double taste, Double finish, Double body) {
        this.aroma = aroma;
        this.taste = taste;
        this.finish = finish;
        this.body = body;
    }
}
