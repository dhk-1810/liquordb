package com.liquordb.like.dto;

import com.liquordb.like.entity.LikeTargetType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorLikeResponseDto {
    private Long id;                    // 좋아요 ID
    private Long userId;                // 좋아요 누른 유저 ID
    private Long liquorId;              // 대상 주류 ID
    private LocalDateTime likedAt;      // 좋아요 누른 시각
}
