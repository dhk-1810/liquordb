package com.liquordb.repository;

import com.liquordb.entity.ReviewLike;
import com.liquordb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    // 총 좋아요 개수 - 리뷰 조회시 사용
    long countByReviewId(Long reviewId);

    // 유저가 좋아요 누른 리뷰 개수 - 마이페이지에서 사용
    long countByUser_IdAndReviewIsHiddenFalse(UUID userId);

    // 유저가 좋이요 누른 리뷰 목록 - 마이페이지에서 사용
    List<ReviewLike> findByUser_IdAndReviewIsHiddenFalse(UUID userId);

    // 유저가 리뷰에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByUserIdAndReviewId(UUID userId, Long reviewId);

    // 유저가 리뷰에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<ReviewLike> findByUserIdAndReviewId(UUID userId, Long reviewId);

    boolean existsByReview_IdAndUser_Id(Long reviewId, UUID userId);

    long countByReview_Id(Long reviewId);

    Optional<ReviewLike> findByReview_IdAndUser_Id(Long reviewId, UUID userId);
}
