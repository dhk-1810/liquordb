package com.liquordb.service;

import com.liquordb.entity.Review;
import com.liquordb.entity.ReviewLike;
import com.liquordb.entity.User;
import com.liquordb.event.ReviewLikeEvent;
import com.liquordb.exception.review.ReviewLikeAlreadyExistsException;
import com.liquordb.exception.review.ReviewLikeNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReviewLikeService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void like(Long reviewId, UUID userId) {

        if (reviewLikeRepository.existsByReview_IdAndUser_Id(reviewId, userId)) {
            throw new ReviewLikeAlreadyExistsException(reviewId, userId);
        }

        User user = userRepository.getReferenceById(userId);
        Review review = reviewRepository.findByIdAndStatusWithUser(reviewId, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewLike reviewLike = ReviewLike.create(user, review);

        try {
            reviewLikeRepository.save(reviewLike);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewNotFoundException(reviewId);
        }

        eventPublisher.publishEvent(new ReviewLikeEvent(reviewId, true, review.getUser().getUsername(), review.getUser().getId()));
    }

    @Transactional
    public void cancelLike(Long reviewId, UUID userId) {

        ReviewLike reviewLike = reviewLikeRepository.findByReview_IdAndUser_Id(reviewId, userId)
                .orElseThrow(() -> new ReviewLikeNotFoundException(reviewId, userId));

        reviewLikeRepository.delete(reviewLike);
        eventPublisher.publishEvent(new ReviewLikeEvent(reviewId, false, null, null));
    }

}
