package com.liquordb.dto.comment;

import com.liquordb.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponseDto (
        Long id,                 // 댓글 ID
        Long reviewId,           // 리뷰 ID
        Long parentId,           // 부모 댓글 ID (없으면 null)
        UUID userId,             // 작성자 ID
        String nickname,         // 작성자 닉네임
        String content,          // 댓글 내용
        Comment.CommentStatus status,       // 게시/숨김/삭제
        long likeCount,           // 좋아요 수
        LocalDateTime createdAt, // 생성 시각
        LocalDateTime updatedAt // 수정 시각
){

}