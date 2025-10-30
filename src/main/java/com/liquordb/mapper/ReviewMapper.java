package com.liquordb.mapper;

import com.liquordb.entity.Liquor;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.File;
import com.liquordb.entity.User;

/**
 * 수동 Mapper 클래스입니다.
 */
public class ReviewMapper {
    public static Review toEntity(ReviewRequestDto dto, User user, Liquor liquor) {
        return Review.builder()
                .user(user)
                .liquor(liquor)
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();
    }

    public static ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .liquorId(review.getLiquor().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .imagePaths(review.getImages().stream().map(File::getFilePath).toList())
                .build();
    }
}
