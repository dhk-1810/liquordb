package com.liquordb.mapper;

import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.File;
import com.liquordb.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * 수동 Mapper 클래스입니다.
 */
public class ReviewMapper {
    public static Review toEntity(ReviewRequestDto request, User user, Liquor liquor) {
        return Review.create(request, user, liquor);
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
                .content(review.getContent())
                .score(review.getRating())
                .liquorName(review.getLiquor().getName())
                .createdDate(review.getCreatedAt())
                .build();

    }
}
