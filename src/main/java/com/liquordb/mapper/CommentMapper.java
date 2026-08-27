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

    public static CommentResponseDto toDto(Comment comment, String userProfileImageUrl, boolean likedByMe, Long nullableReplyCount) {
        com.liquordb.entity.User user = comment.getUser();
        UUID userId = user != null ? user.getId() : null;
        String username = user != null ? user.getUsername() : "탈퇴한 사용자";
        String profileUrl = user != null ? userProfileImageUrl : null;
        Long parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        long replyCount = (nullableReplyCount != null) ? nullableReplyCount : 0L;

        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(userId)
                .username(username)
                .userProfileImageUrl(profileUrl)
                .reviewId(comment.getReview().getId())
                .liquorId(comment.getReview().getLiquor().getId()) // TODO 쿼리 많이나감
                .reviewTitle(comment.getReview().getTitle())
                .parentId(parentId)
                .content(comment.getContent())
                .status(comment.getStatus())
                .likeCount(comment.getLikeCount())
                .likedByMe(likedByMe)
                .replyCount(replyCount)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();

    }

}
