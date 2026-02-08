package com.liquordb.repository;

import com.liquordb.entity.Comment;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 댓글 단건 조회
    Optional<Comment> findByIdAndStatus(Long id, Comment.CommentStatus status);

    // 특정 리뷰에 달린 댓글 조회
    Slice<Comment> findByReview_IdAndStatus(Long reviewId, Comment.CommentStatus status, Pageable pageable);

    // 특정 유저가 작성한 댓글 조회 (삭제한 댓글은 제외)
    Page<Comment> findByUserIdAndStatus(UUID userId, Comment.CommentStatus statuses, Pageable pageable);

    // 특정 유저가 작성한 댓글 수
    long countByUserAndStatus(User user, Comment.CommentStatus status);

    // 특정 유저가 작성한 댓글
    List<Comment> findAllByUser_Id(UUID userId);

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

    // 리뷰 연관 댓글 soft delete (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Comment c SET c.status = Comment.CommentStatus.DELETED, c.deletedAt = :reviewDeletedAt
        WHERE c.review = :review AND c.status != Comment.CommentStatus.DELETED
    """)
    void softDeleteCommentsByReview(@Param("review") Review review, @Param("reviewDeletedAt") LocalDateTime reviewDeletedAt);

    // 리뷰 연관 댓글 restore (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
         UPDATE Comment c SET c.status = Comment.CommentStatus.ACTIVE, c.deletedAt = null
         WHERE c.review = :review AND c.deletedAt = :reviewDeletedAt
    """)
    void restoreCommentsByReview(@Param("review") Review review, @Param("reviewDeletedAt") LocalDateTime reviewDeletedAt);

    // 주류 연관 댓글 soft delete (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Comment c SET c.status = Comment.CommentStatus.DELETED, c.deletedAt = :liquorDeletedAt
        WHERE c.review.id in (SELECT r.id FROM Review r WHERE r.liquor = :liquor)
            AND c.status != Comment.CommentStatus.DELETED
    """)
    void softDeleteCommentsByLiquor(@Param("liquor") Liquor liquor, @Param("liquorDeletedAt") LocalDateTime liquorDeletedAt);

    // 주류 연관 댓글 restore (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
         UPDATE Comment c SET c.status = Comment.CommentStatus.ACTIVE, c.deletedAt = null
         WHERE c.review.id in (SELECT r.id FROM Review r WHERE r.liquor = :liquor)
             AND c.deletedAt = :liquorDeletedAt
    """)
    void restoreCommentsByLiquor(@Param("liquor") Liquor liquor, @Param("liquorDeletedAt") LocalDateTime liquorDeletedAt);

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + :delta WHERE c.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}