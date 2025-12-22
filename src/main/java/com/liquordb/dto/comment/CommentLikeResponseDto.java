package com.liquordb.dto.comment;

import com.liquordb.entity.CommentLike;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentLikeResponseDto (
        Long id,             // 좋아요 ID // TODO 필요한가?
        UUID userId,                // 좋아요 누른 유저 ID
        Long commentId,              // 대상 댓글 ID
        LocalDateTime likedAt      // 좋아요 누른 시각
){
    public static CommentLikeResponseDto toDto(CommentLike commentLike) {
        return CommentLikeResponseDto.builder()
                .id(commentLike.getId())
                .userId(commentLike.getUser().getId())
                .commentId(commentLike.getComment().getId())
                .likedAt(commentLike.getLikedAt())
                .build();
    }
}