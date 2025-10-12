package com.liquordb.dto.user;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import lombok.*;

import java.util.List;

/**
 * 유저 마이페이지 응답 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserMyPageResponseDto {
    private Long userId;
    private String email;
    private String nickname;

    private long likedLiquorCount;     // 주류 좋아요 수
    private long likedReviewCount;     // 리뷰 좋아요 수
    private long likedCommentCount;    // 댓글 좋아요 수
    private long reviewCount;          // 작성 리뷰 수
    private long commentCount;         // 작성 댓글 수

    private List<ReviewSummaryDto> reviewList; // 작성한 리뷰 목록
    private List<CommentResponseDto> commentList; // 작성 댓글 목록

    private List<LiquorSummaryDto> likedLiquors;     // 좋아요 누른 주류 목록
    private List<ReviewSummaryDto> likedReviews;    // 좋아요 누른 리뷰 목록
    private List<CommentResponseDto> likedComments;  // 좋아요 누른 댓글 목록

    private List<String> preferredTags;
}