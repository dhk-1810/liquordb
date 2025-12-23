package com.liquordb.dto.comment;

import com.liquordb.entity.CommentLike;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentLikeResponseDto (
        Long id, // TODO 필요한가?
        UUID userId,
        Long commentId,
        LocalDateTime likedAt
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