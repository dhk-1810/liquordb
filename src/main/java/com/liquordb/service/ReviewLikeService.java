package com.liquordb.service;

import com.liquordb.dto.LikeResponseDto;
import com.liquordb.entity.*;
import com.liquordb.event.ReviewLikeEvent;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.repository.UserRepository;
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
    public LikeResponseDto like(Long reviewId, UUID userId) {

        if (reviewLikeRepository.existsByReview_IdAndUser_Id(reviewId, userId)) {
            return new LikeResponseDto(true, reviewLikeRepository.countByReview_Id(reviewId));
        }

        User user = userRepository.getReferenceById(userId);
        Review review = reviewRepository.getReferenceById(reviewId);

        ReviewLike reviewLike = ReviewLike.create(user, review);

        try {
            reviewLikeRepository.save(reviewLike);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewNotFoundException(reviewId);
        }

        eventPublisher.publishEvent(new ReviewLikeEvent(reviewId, true));

        long likeCount = reviewLikeRepository.countByReview_Id(reviewId);
        return new LikeResponseDto(true, likeCount);
    }

    @Transactional
    public LikeResponseDto cancelLike(Long reviewId, UUID userId) {
        reviewLikeRepository.findByReview_IdAndUser_Id(reviewId, userId)
                .ifPresent(reviewLikeRepository::delete);

        eventPublisher.publishEvent(new ReviewLikeEvent(reviewId, false));
        long likeCount = reviewLikeRepository.countByReview_Id(reviewId);
        return new LikeResponseDto(false, likeCount);
    }

}
