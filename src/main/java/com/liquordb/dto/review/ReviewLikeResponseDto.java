package com.liquordb.dto.review;

import com.liquordb.entity.ReviewLike;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewLikeResponseDto (
        Long id,                    // 좋아요 ID
        UUID userId,                // 좋아요 누른 유저 ID
        Long reviewId              // 대상 리뷰 ID
){
    public static ReviewLikeResponseDto toDto (ReviewLike reviewLike) {
        return ReviewLikeResponseDto.builder()
                .userId(reviewLike.getUser().getId())
                .reviewId(reviewLike.getReview().getId())
                .build();
    }
}
