package com.liquordb.like.mapper;

import com.liquordb.like.dto.CommentLikeResponseDto;
import com.liquordb.like.dto.LiquorLikeResponseDto;
import com.liquordb.like.dto.ReviewLikeResponseDto;
import com.liquordb.like.entity.CommentLike;
import com.liquordb.like.entity.LiquorLike;
import com.liquordb.like.entity.ReviewLike;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.review.entity.Comment;
import com.liquordb.review.entity.Review;
import com.liquordb.user.entity.User;

import java.time.LocalDateTime;

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

    public static LiquorLikeResponseDto toLiquorLikeResponseDto(Long id, Long userId, Long liquorId, LocalDateTime likedAt) {
        return LiquorLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .liquorId(liquorId)
                .likedAt(likedAt)
                .build();
    }

    public static ReviewLikeResponseDto toReviewLikeResponseDto(Long id, Long userId, Long reviewId, LocalDateTime likedAt) {
        return ReviewLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .reviewId(reviewId)
                .likedAt(likedAt)
                .build();
    }

    public static CommentLikeResponseDto toCommentLikeResponseDto(Long id, Long userId, Long commentId, LocalDateTime likedAt) {
        return CommentLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .commentId(commentId)
                .likedAt(likedAt)
                .build();
    }
}