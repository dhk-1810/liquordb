package com.liquordb.review.mapper;

import com.liquordb.liquor.entity.Liquor;
import com.liquordb.review.dto.ReviewRequestDto;
import com.liquordb.review.dto.ReviewResponseDto;
import com.liquordb.review.entity.Review;
import com.liquordb.user.entity.User;

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
                .build();
    }
}
