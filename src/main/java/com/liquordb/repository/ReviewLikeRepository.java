package com.liquordb.repository;

import com.liquordb.entity.Review;
import com.liquordb.entity.ReviewLike;
import com.liquordb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    long countByReview_Id(Long reviewId);

    long countByUser_IdAndReviewStatus(UUID userId, Review.ReviewStatus reviewStatus);

    boolean existsByReview_IdAndUser_Id(Long reviewId, UUID userId);

    Optional<ReviewLike> findByReview_IdAndUser_Id(Long reviewId, UUID userId);

    @org.springframework.data.jpa.repository.Query("select rl.review.id from ReviewLike rl where rl.user.id = :userId and rl.review.id in :reviewIds")
    java.util.Set<Long> findLikedReviewIdsByUserIdAndReviewIds(
            @org.springframework.data.repository.query.Param("userId") UUID userId,
            @org.springframework.data.repository.query.Param("reviewIds") List<Long> reviewIds
    );
}
