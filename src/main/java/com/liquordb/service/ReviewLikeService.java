package com.liquordb.service;

import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.entity.ReviewLike;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public ReviewLikeResponseDto toggleLike(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 리뷰입니다."));

        ReviewLike existing = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)
                .orElse(null);

        if (existing != null) {
            reviewLikeRepository.delete(existing);
            return buildResponse(
                    existing.getId(),
                    userId,
                    reviewId,
                    null);
        } else {
            ReviewLike newLike = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .likedAt(LocalDateTime.now())
                    .build();

            ReviewLike saved = reviewLikeRepository.save(newLike);

            return buildResponse(
                    saved.getId(),
                    userId,
                    reviewId,
                    saved.getLikedAt());
        }
    }

    @Transactional(readOnly = true)
    public long countLikes(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    // ?
    private ReviewLikeResponseDto buildResponse(Long id, Long userId, Long reviewId, LocalDateTime likedAt) {
        return ReviewLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .reviewId(reviewId)
                .likedAt(likedAt)
                .build();
    }
}
