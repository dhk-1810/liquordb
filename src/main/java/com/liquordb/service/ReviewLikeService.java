package com.liquordb.service;

import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.entity.ReviewLike;
import com.liquordb.entity.UserStatus;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.ReviewMapper;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public ReviewLikeResponseDto toggleLike(UUID userId, Long reviewId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

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

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewSummaryDtosByUserId(UUID userId){
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        return reviewLikeRepository.findByUserAndReviewIsHiddenFalse(user).stream()
                .map(reviewLike -> ReviewMapper.toDto(reviewLike.getReview()))
                .toList();
    }

    // DTO 빌드
    private ReviewLikeResponseDto buildResponse(Long id, UUID userId, Long reviewId, LocalDateTime likedAt) {
        return ReviewLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .reviewId(reviewId)
                .likedAt(likedAt)
                .build();
    }
}
