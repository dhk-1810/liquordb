package com.liquordb.like.dto;

import com.liquordb.like.entity.LikeTargetType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private Long id;                    // 좋아요 ID
    private Long userId;                // 좋아요 누른 유저 ID
    private Long targetId;              // 좋아요 대상 ID (주류, 리뷰, 댓글)
    private LikeTargetType targetType;  // 좋아요 대상 타입 (REVIEW, COMMENT 등)
    private LocalDateTime likedAt;      // 좋아요 누른 시각
}
