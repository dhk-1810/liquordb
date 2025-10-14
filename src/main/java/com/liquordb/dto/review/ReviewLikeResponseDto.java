package com.liquordb.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLikeResponseDto {
    private Long id;                    // 좋아요 ID
    private UUID userId;                // 좋아요 누른 유저 ID
    private Long reviewId;              // 대상 리뷰 ID
    private LocalDateTime likedAt;      // 좋아요 누른 시각
}
