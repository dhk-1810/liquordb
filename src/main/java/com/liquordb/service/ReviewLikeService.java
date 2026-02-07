package com.liquordb.service;

import com.liquordb.dto.LikeResponseDto;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.repository.ReviewRepository;
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

    @Transactional
    public LikeResponseDto like(Long reviewId, UUID userId) {

        if (reviewLikeRepository.existsByReview_IdAndUser_Id(reviewId, userId)) {
            return new LikeResponseDto(true, reviewLikeRepository.countByReview_Id(reviewId));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        ReviewLike reviewLike = ReviewLike.create(user, review);
        reviewLikeRepository.save(reviewLike);
        long likeCount = reviewLikeRepository.countByReview_Id(reviewId);
        return new LikeResponseDto(true, likeCount);
    }

    @Transactional
    public LikeResponseDto cancelLike(Long reviewId, UUID userId) {
        reviewLikeRepository.findByReview_IdAndUser_Id(reviewId, userId)
                .ifPresent(reviewLikeRepository::delete);

        long likeCount = reviewLikeRepository.countByReview_Id(reviewId);
        return new LikeResponseDto(false, likeCount);
    }

}
