package com.liquordb.repository;

import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 리뷰에 달린 댓글 조회 (삭제되지 않은 댓글만)
    Page<Comment> findByReviewIdAndStatus(Long reviewId, Comment.CommentStatus status, Pageable pageable);

    // 특정 유저가 작성한 댓글 조회 (삭제한 댓글은 제외)
    Page<Comment> findByUserIdAndStatus(UUID userId, Comment.CommentStatus statuses, Pageable pageable);

    // 특정 유저가 작성한 댓글 수
    long countByUserAndSuaFalse(User user);

    // 관리자용 - 유저ID나 상태로 리뷰 목록 조회
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
