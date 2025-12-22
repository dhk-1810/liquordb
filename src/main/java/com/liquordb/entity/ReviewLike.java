package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_likes")
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void onCreate() {
        likedAt = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public ReviewLike(User user, Review review, LocalDateTime likedAt) {
        this.user = user;
        this.review = review;
        this.likedAt = likedAt;
    }

    public static ReviewLike create(User user, Review review) {
        return ReviewLike.builder()
                .user(user)
                .review(review)
                .build();
    }
}