package com.liquordb.service;

import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.entity.ReviewLike;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        User user = userRepository.findByIdAndStatus(userId, UserStatus.BANNED)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        ReviewLike existingReviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)
                .orElse(null);

        if (existingReviewLike != null) {
            reviewLikeRepository.delete(existingReviewLike);
            review.decreaseLikeCount();
            return null;
        } else {
            ReviewLike reviewLike = ReviewLike.create(user, review);
            reviewLikeRepository.save(reviewLike);
            review.increaseLikeCount();
            return ReviewLikeResponseDto.toDto(reviewLike);
        }
    }

}
