package com.liquordb.dto.comment;

import com.liquordb.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponseDto(
        Long id,
        Long reviewId,
        Long liquorId,
        String reviewTitle,
        Long parentId,
        UUID userId,
        String username,
        String userProfileImageUrl,
        String content,
        Comment.CommentStatus status,
        long likeCount,
        boolean likedByMe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}