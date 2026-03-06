package com.liquordb.repository.review;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {

    // 리뷰 단건 조회
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.imageKeys WHERE r.id = :id")
    Optional<Review> findByIdWithImageKeys(@Param("id") Long id);

    Optional<Review> findByIdAndStatus(Long id, Review.ReviewStatus status);

    // 리뷰 + 주류 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.liquor WHERE r.id = :reviewId AND r.status != :deleted")
    Optional<Review> findByIdWithLiquorAndStatusNot(
            @Param("reviewId") Long reviewId,
            @Param("deleted") Review.ReviewStatus deleted
    );

    // 좋아요 누른 리뷰 개수
    long countByUser_IdAndStatus(UUID userId, Review.ReviewStatus status);

    // 주류 연관 리뷰 soft delete (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Review r SET r.status = :deleted, r.deletedAt = :liquorDeletedAt
        WHERE r.liquor = :liquor AND r.status != :deleted
    """)
    void softDeleteReviewsByLiquor(
            @Param("liquor") Liquor liquor,
            @Param("liquorDeletedAt") LocalDateTime liquorDeletedAt,
            @Param("deleted") Review.ReviewStatus deleted
    );

    // 주류 연관 리뷰 restore (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
         UPDATE Review r SET r.status = :active, r.deletedAt = null
         WHERE r.liquor = :liquor AND r.deletedAt = :deletedAt
    """)
    void restoreReviewsByLiquor(
            @Param("liquor") Liquor liquor,
            @Param("active") Review.ReviewStatus active
    );

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Review r SET r.likeCount = r.likeCount + :delta WHERE r.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
