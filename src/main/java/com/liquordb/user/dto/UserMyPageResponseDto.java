package com.liquordb.user.dto;

import com.liquordb.liquor.dto.LiquorSummaryDto;
import com.liquordb.review.dto.CommentResponseDto;
import com.liquordb.review.dto.ReviewResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 유저 마이페이지 응답 DTO입니다.
 */
@Data
@Builder
public class UserMyPageResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String level;

    private long likedLiquorCount;     // 주류 좋아요 수
    private long likedReviewCount;     // 리뷰 좋아요 수
    private long likedCommentCount;    // 댓글 좋아요 수
    private long reviewCount;          // 작성 리뷰 수
    private long commentCount;         // 작성 댓글 수

    private List<LiquorSummaryDto> likedLiquors;     // 좋아요 누른 주류 목록
    private List<ReviewResponseDto> likedReviews;    // 좋아요 누른 리뷰 목록
    private List<CommentResponseDto> likedComments;  // 좋아요 누른 댓글 목록
    private List<LiquorSummaryDto> reviewedLiquors;  // 리뷰 작성한 주류 목록
    private List<LiquorSummaryDto> commentedLiquors; // 댓글 작성한 주류 목록

    private List<String> preferredTags;
}