package com.liquordb.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewLikeResponseDto (
        Long id,                    // 좋아요 ID
        UUID userId,                // 좋아요 누른 유저 ID
        Long reviewId,              // 대상 리뷰 ID
        LocalDateTime likedAt      // 좋아요 누른 시각
){

}
