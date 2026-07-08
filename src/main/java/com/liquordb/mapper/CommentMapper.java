package com.liquordb.mapper;

import com.liquordb.dto.comment.request.CommentRequest;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import java.util.UUID;

public class CommentMapper {

    public static Comment toEntity(CommentRequest request, Comment parent, Review review, User requestUser) {
        return Comment.create(
                request.content(),
                review,
                parent,
                requestUser
        );
    }

    public static CommentResponseDto toDto(Comment comment, String userProfileImageUrl, boolean likedByMe) {
            com.liquordb.entity.User user = comment.getUser();
            UUID userId = user != null ? user.getId() : null;
            String username = user != null ? user.getUsername() : "탈퇴한 사용자";
            String profileUrl = user != null ? userProfileImageUrl : null;

            return CommentResponseDto.builder()
                    .id(comment.getId())
                    .userId(userId)
                    .username(username)
                    .userProfileImageUrl(profileUrl)
                    .reviewId(comment.getReview().getId())
                    .liquorId(comment.getReview().getLiquor().getId())
                    .reviewTitle(comment.getReview().getTitle())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .content(comment.getContent())
                    .status(comment.getStatus())
                    .likeCount(comment.getLikeCount())
                    .likedByMe(likedByMe)
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();

    }

}
