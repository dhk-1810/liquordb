package com.liquordb.entity;

import com.liquordb.entity.id.ReviewLikeId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@IdClass(ReviewLikeId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_likes")
public class ReviewLike {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
    }

    public static ReviewLike create(User user, Review review) {
        return new ReviewLike(user, review);
    }
}