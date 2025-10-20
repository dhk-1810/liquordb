package com.liquordb.mapper;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.Comment;

public class CommentMapper {

    public static CommentResponseDto toDto(Comment comment) {
            return CommentResponseDto.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .nickname(comment.getUser().getNickname())
                    .reviewId(comment.getReview().getId())
                    .parentId(comment.getParent().getId())
                    .content(comment.getContent())
                    .status(comment.getStatus())
                    .likeCount(comment.getLikes().size())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();

    }
}
