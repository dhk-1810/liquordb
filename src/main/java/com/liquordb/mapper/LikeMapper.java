package com.liquordb.mapper;

import com.liquordb.dto.comment.CommentLikeResponseDto;
import com.liquordb.dto.liquor.LiquorLikeResponseDto;
import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.entity.CommentLike;
import com.liquordb.entity.LiquorLike;
import com.liquordb.entity.ReviewLike;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class LikeMapper {

    public static LiquorLike toLiquorLike(User user, Liquor liquor) {
        return LiquorLike.builder()
                .user(user)
                .liquor(liquor)
                .likedAt(LocalDateTime.now())
                .build();
    }

    public static ReviewLike toReviewLike(User user, Review review) {
        return ReviewLike.builder()
                .user(user)
                .review(review)
                .likedAt(LocalDateTime.now())
                .build();
    }

    public static CommentLike toCommentLike(User user, Comment comment) {
        return CommentLike.builder()
                .user(user)
                .comment(comment)
                .likedAt(LocalDateTime.now())
                .build();
    }

    public static LiquorLikeResponseDto toLiquorLikeResponseDto(Long id, UUID userId, Long liquorId, LocalDateTime likedAt) {
        return LiquorLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .liquorId(liquorId)
                .likedAt(likedAt)
                .build();
    }

    public static ReviewLikeResponseDto toReviewLikeResponseDto(Long id, UUID userId, Long reviewId, LocalDateTime likedAt) {
        return ReviewLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .reviewId(reviewId)
                .likedAt(likedAt)
                .build();
    }

    public static CommentLikeResponseDto toCommentLikeResponseDto(Long id, UUID userId, Long commentId, LocalDateTime likedAt) {
        return CommentLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .commentId(commentId)
                .likedAt(likedAt)
                .build();
    }
}