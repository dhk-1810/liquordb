package com.liquordb.mapper;

import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.ReviewNotFoundException;
import com.liquordb.exception.comment.InvalidParentCommentException;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    public Comment toEntity(User requestUser, CommentRequestDto request) {
        Long reviewId = request.reviewId();
        Review review = reviewRepository.findByIdAndStatus_Active(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Comment parent = null;
        Long parentId = request.parentId();
        if (parentId != null) {
            parent = commentRepository.findByIdAndStatus_Active(parentId)
                    .orElseThrow(() -> new CommentNotFoundException(parentId));
        }
        if (parent != null && !parent.getReview().getId().equals(reviewId)) {
            throw new InvalidParentCommentException(parentId);
        }

        return Comment.create(request.content(), review, parent, requestUser);
    }

    public CommentResponseDto toDto(Comment comment) {
            return CommentResponseDto.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .nickname(comment.getUser().getNickname())
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
