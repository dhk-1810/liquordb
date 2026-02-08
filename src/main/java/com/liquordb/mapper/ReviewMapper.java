package com.liquordb.mapper;

import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.File;
import com.liquordb.entity.User;
import com.liquordb.entity.reviewdetail.ReviewDetail;

import java.util.Collections;
import java.util.List;

public class ReviewMapper {
    public static Review toEntity(ReviewRequestDto request, Liquor liquor, User user) {
        Review review = Review.create(request, liquor, user);
        ReviewDetail detail = ReviewDetailMapper.toEntity(request.reviewDetailRequest(), review);
        review.addDetail(detail);
        return review;
    }

    public static ReviewResponseDto toDto(Review review) {
        List<String> imagePaths = review.getImages() != null
                ? review.getImages().stream().map(File::getFilePath).toList()
                : Collections.emptyList();

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .liquorId(review.getLiquor().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .imagePaths(imagePaths)
                .build();
    }

    public static ReviewSummaryDto toSummaryDto(Review review) {
        return ReviewSummaryDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .score(review.getRating())
                .liquorName(review.getLiquor().getName())
                .createdAt(review.getCreatedAt())
                .build();

    }
}
