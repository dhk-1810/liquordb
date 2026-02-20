package com.liquordb.repository.review;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.Review;
import com.liquordb.repository.comment.CustomCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {

    // 리뷰 단건 조회
    Optional<Review> findByIdAndStatus(Long id, Review.ReviewStatus status);
    Optional<Review> findByIdAndStatusNot(Long id, Review.ReviewStatus status);

    // 리뷰 + 주류 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.liquor WHERE r.id = :reviewId AND r.status <> 'DELETED'")
    Optional<Review> findByIdWithLiquor(@Param("reviewId") Long reviewId);

    // 좋아요 누른 리뷰 개수
    long countByUser_IdAndStatus(UUID userId, Review.ReviewStatus status);

    // 주류 연관 리뷰 soft delete (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Review r SET r.status = Review.ReviewStatus.DELETED, r.deletedAt = :liquorDeletedAt
        WHERE r.liquor = :liquor AND r.status != Review.ReviewStatus.DELETED
    """)
    void softDeleteReviewsByLiquor(@Param("liquor") Liquor liquor,  @Param("liquorDeletedAt") LocalDateTime liquorDeletedAt);

    // 주류 연관 리뷰 restore (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
         UPDATE Review r SET r.status = Review.ReviewStatus.ACTIVE, r.deletedAt = null
         WHERE r.liquor = :liquor AND r.deletedAt = :deletedAt
    """)
    void restoreReviewsByLiquor(@Param("liquor") Liquor liquor);

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Review r SET r.likeCount = r.likeCount + :delta WHERE r.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
