package com.liquordb.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLikeResponseDto {
    private Long id;                    // 좋아요 ID
    private Long userId;                // 좋아요 누른 유저 ID
    private Long commentId;              // 대상 댓글 ID
    private LocalDateTime likedAt;      // 좋아요 누른 시각
}
