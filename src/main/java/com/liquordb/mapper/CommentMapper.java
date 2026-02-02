package com.liquordb.mapper;

import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.comment.InvalidParentCommentException;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class CommentMapper {

    public static Comment toEntity(CommentRequestDto request, Comment parent, Review review, User requestUser) {
        return Comment.create(
                request.content(),
                review,
                parent,
                requestUser
        );
    }

    public static CommentResponseDto toDto(Comment comment) {
            return CommentResponseDto.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .username(comment.getUser().getUsername())
                    .reviewId(comment.getReview().getId())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .content(comment.getContent())
                    .status(comment.getStatus())
                    .likeCount(comment.getLikeCount())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();

    }

}
