package com.liquordb.entity;

import com.liquordb.entity.id.ReviewImageKeyId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review_image_key")
public class ReviewImageKey {

    @EmbeddedId
    private ReviewImageKeyId id;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public ReviewImageKey(Review review, String imageKey) {
        this.id = new ReviewImageKeyId(review.getId(), imageKey);
        this.review = review;
    }
}
