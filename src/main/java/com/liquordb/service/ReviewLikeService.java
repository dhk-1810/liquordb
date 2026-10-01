package com.liquordb.service;

import com.liquordb.entity.Review;
import com.liquordb.entity.ReviewLike;
import com.liquordb.entity.User;
import com.liquordb.exception.review.ReviewLikeAlreadyExistsException;
import com.liquordb.exception.review.ReviewLikeNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.review.SelfReviewLikeException;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReviewLikeService {

    private static final String REVIEW_LIKE_MESSAGE_SUFFIX = "님이 내 리뷰를 좋아합니다.";

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // 단방향 참조

    @Transactional
    public void like(Long reviewId, UUID userId) {

        if (reviewLikeRepository.existsByReview_IdAndUser_Id(reviewId, userId)) {
            throw new ReviewLikeAlreadyExistsException(reviewId, userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Review review = reviewRepository.findByIdAndStatusWithUser(reviewId, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (review.getUser().getId().equals(userId)) {
            throw new SelfReviewLikeException(reviewId, userId);
        }

        ReviewLike reviewLike = ReviewLike.create(user, review);

        try {
            reviewLikeRepository.saveAndFlush(reviewLike);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewNotFoundException(reviewId);
        }

        reviewRepository.updateLikeCount(reviewId, 1);
        notificationService.sendNotification(review.getUser().getId(), user.getUsername() + REVIEW_LIKE_MESSAGE_SUFFIX);
    }

    @Transactional
    public void cancelLike(Long reviewId, UUID userId) {

        ReviewLike reviewLike = reviewLikeRepository.findByReview_IdAndUser_Id(reviewId, userId)
                .orElseThrow(() -> new ReviewLikeNotFoundException(reviewId, userId));

        reviewLikeRepository.delete(reviewLike);
        reviewLikeRepository.flush();
        reviewRepository.updateLikeCount(reviewId, -1);
    }

}
