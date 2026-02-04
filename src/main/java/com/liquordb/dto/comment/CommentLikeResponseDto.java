package com.liquordb.dto.comment;

import com.liquordb.entity.CommentLike;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentLikeResponseDto (
        UUID userId,
        Long commentId
){
    public static CommentLikeResponseDto toDto(CommentLike commentLike) {
        return CommentLikeResponseDto.builder()
                .userId(commentLike.getUser().getId())
                .commentId(commentLike.getComment().getId())
                .build();
    }
}