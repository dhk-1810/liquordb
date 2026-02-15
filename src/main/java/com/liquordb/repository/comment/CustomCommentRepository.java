package com.liquordb.repository.comment;

import com.liquordb.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CustomCommentRepository {

    // 특정 리뷰에 달린 댓글 조회
    Slice<Comment> findByReview_IdAndStatus(Long reviewId, Comment.CommentStatus status, Pageable pageable);

    // 특정 유저가 작성한 댓글 조회 (삭제한 댓글은 제외)
    Page<Comment> findByUserIdAndStatus(UUID userId, Comment.CommentStatus statuses, Pageable pageable);

    // [관리자용] 댓글 전체 조회 - 유저별로 필터링(선택), 상태별로 필터링(선택)
    @Query("""
        SELECT c FROM Comment c
        WHERE (:userId IS NULL OR c.user.id = :userId)
        AND (:status IS NULL OR c.status = :status)
    """)
    Page<Comment> findAllByOptionalFilters(
            @Param("userId") UUID userId,
            @Param("status") Comment.CommentStatus status,
            Pageable pageable
    );
}
