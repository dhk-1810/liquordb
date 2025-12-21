package com.liquordb.dto.user;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * 유저 마이페이지 응답 DTO
 */
@Builder
public record UserMyPageResponseDto (
        UUID userId,
        String email,
        String nickname,

        long likedLiquorCount,     // 주류 좋아요 수
        long likedReviewCount,     // 리뷰 좋아요 수
        long likedCommentCount,    // 댓글 좋아요 수
        long reviewCount,          // 작성 리뷰 수
        long commentCount,         // 작성 댓글 수

        List<ReviewSummaryDto> reviewList, // 작성한 리뷰 목록
        List<CommentResponseDto> commentList, // 작성 댓글 목록

        List<LiquorSummaryDto> likedLiquors,     // 좋아요 누른 주류 목록
        List<ReviewResponseDto> likedReviews,    // 좋아요 누른 리뷰 목록
        List<CommentResponseDto> likedComments,  // 좋아요 누른 댓글 목록

        List<TagResponseDto> preferredTags
){

}