package com.liquordb.repository.comment;

import com.liquordb.entity.Comment;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    // 댓글 단건 조회
    Optional<Comment> findByIdAndStatus(Long id, Comment.CommentStatus status);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.id = :commentId AND c.status = :status")
    Optional<Comment> findByIdWAndStatusWithUser(@Param("commentId") Long commentId, @Param("status") Comment.CommentStatus status);

    // 특정 유저가 작성한 댓글 수
    long countByUser_IdAndStatus(UUID userId, Comment.CommentStatus status);

    // 리뷰 연관 댓글 soft delete (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Comment c SET c.status = :deleted, c.deletedAt = :reviewDeletedAt
        WHERE c.review = :review AND c.status != :deleted
    """)
    void softDeleteCommentsByReview(
            @Param("review") Review review,
            @Param("reviewDeletedAt") LocalDateTime reviewDeletedAt,
            @Param("deleted") Comment.CommentStatus deleted
    );

    // 리뷰 연관 댓글 restore (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
         UPDATE Comment c SET c.status = :active, c.deletedAt = null
         WHERE c.review = :review AND c.deletedAt = :reviewDeletedAt
    """)
    void restoreCommentsByReview(
            @Param("review") Review review,
            @Param("reviewDeletedAt") LocalDateTime reviewDeletedAt,
            @Param("active") Comment.CommentStatus active
    );

    // 주류 연관 댓글 soft delete (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Comment c SET c.status = :deleted, c.deletedAt = :liquorDeletedAt
        WHERE c.review.id in (SELECT r.id FROM Review r WHERE r.liquor = :liquor)
            AND c.status != :deleted
    """)
    void softDeleteCommentsByLiquor(
            @Param("liquor") Liquor liquor,
            @Param("liquorDeletedAt") LocalDateTime liquorDeletedAt,
            @Param("deleted") Comment.CommentStatus deleted
    );

    // 주류 연관 댓글 restore (Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("""
         UPDATE Comment c SET c.status = :active, c.deletedAt = null
         WHERE c.review.id in (SELECT r.id FROM Review r WHERE r.liquor = :liquor)
             AND c.deletedAt = :liquorDeletedAt
    """)
    void restoreCommentsByLiquor(
            @Param("liquor") Liquor liquor,
            @Param("liquorDeletedAt") LocalDateTime liquorDeletedAt,
            @Param("active") Comment.CommentStatus active
    );

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + :delta WHERE c.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

}