package com.liquordb.review.dto;

import com.liquordb.review.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {

    private Long id;                 // 댓글 ID
    private Long reviewId;           // 리뷰 ID
    private Long parentId;           // 부모 댓글 ID (없으면 null)
    private Long userId;             // 작성자 ID
    private String nickname;         // 작성자 닉네임
    private String content;          // 댓글 내용
    private boolean isDeleted;       // 삭제 여부
    private long likeCount;           // 좋아요 수
    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime updatedAt; // 수정 시각

    public static CommentResponseDto from(Comment comment, long likeCount) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .reviewId(comment.getReview().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted())
                .likeCount(likeCount)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}