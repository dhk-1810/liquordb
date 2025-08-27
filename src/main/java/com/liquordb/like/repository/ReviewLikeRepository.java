package com.liquordb.like.repository;

import com.liquordb.like.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    // 총 좋아요 개수 - 리뷰 조회시 사용
    long countByReviewId(Long reviewId);

    // 유저가 좋아요 누른 리뷰 개수 - 마이페이지에서 사용
    long countByUserId(Long userId);

    // 유저가 좋이요 누른 리뷰 목록 - 마이페이지에서 사용
    List<ReviewLike> findByUserId(Long userId);

    // 유저가 리뷰에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

    // 유저가 리뷰에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);
}
