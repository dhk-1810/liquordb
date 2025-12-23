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

        long likedLiquorCount,
        long likedReviewCount,
        long likedCommentCount,
        long reviewCount,
        long commentCount,

        List<ReviewSummaryDto> reviewList,
        List<CommentResponseDto> commentList,

        List<LiquorSummaryDto> likedLiquors,
        List<ReviewResponseDto> likedReviews,
        List<CommentResponseDto> likedComments,

        List<TagResponseDto> preferredTags
){

}