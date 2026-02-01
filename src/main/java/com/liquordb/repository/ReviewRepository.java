package com.liquordb.repository;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 단건 조회
    Optional<Review> findByIdAndStatus(Long id, Review.ReviewStatus status);
    Optional<Review> findByIdAndStatusNot(Long id, Review.ReviewStatus status);

    // 리뷰 평균 평점
    // TODO 생성, 수정, 삭제시에 알아서 변경되게 할까싶음
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.liquor.id = :liquorId")
    Double getAverageRatingByLiquorId(@Param("liquorId") Long liquorId);

    // 리뷰 단건 조회
    Optional<Review> findByIdAndIsHiddenFalse(Long id);

    // 특정 주류, 유저에 따른 리뷰 목록
    List<Review> findAllByLiquor_IdAndStatus(Long id, Review.ReviewStatus status); // TODO 주류 리뷰 모두 숨김 상태 아니여야 함.
    Page<Review> findAllByLiquor_IdAndStatus(Pageable pageable, Long liquorId, Review.ReviewStatus status);
    Page<Review> findAllByUser_IdAndStatus(Pageable pageable, UUID userId, Review.ReviewStatus status);

    // 좋아요 누른 리뷰 개수
    long countByUserAndStatus(User user, Review.ReviewStatus status);

    // 특정 유저가 작성한 댓글
    List<Review> findAllByUser_IdAndStatus(UUID reviewId, Review.ReviewStatus status);

    // 관리자용 - 유저ID나 상태로 리뷰 목록 조회
    @Query("""
    SELECT r FROM Review r
    WHERE (:userId IS NULL OR r.user.id = :userId)
    AND (:status IS NULL OR r.status = :status)
    """)
    Page<Review> findAllByOptionalFilters(
            @Param("userId") UUID userId,
            @Param("status") Review.ReviewStatus status,
            Pageable pageable
    );

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


}
