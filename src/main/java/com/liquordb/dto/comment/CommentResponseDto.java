package com.liquordb.dto.comment;

import com.liquordb.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponseDto (
        Long id,
        Long reviewId,
        Long parentId,
        UUID userId,
        String username,
        String content,
        Comment.CommentStatus status,
        long likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}