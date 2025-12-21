package com.liquordb.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentLikeResponseDto (
        Long id,             // 좋아요 ID
        UUID userId,                // 좋아요 누른 유저 ID
        Long commentId,              // 대상 댓글 ID
        LocalDateTime likedAt      // 좋아요 누른 시각
){

}