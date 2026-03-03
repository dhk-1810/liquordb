package com.liquordb.entity;

import com.liquordb.entity.id.ReviewLikeId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_likes")
public class ReviewLike {

    @EmbeddedId
    private ReviewLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public ReviewLike(User user, Review review) {
        this.id = new ReviewLikeId(user.getId(), review.getId());
        this.user = user;
        this.review = review;
    }

    public static ReviewLike create(User user, Review review) {
        return new ReviewLike(user, review);
    }
}