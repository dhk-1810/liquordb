package com.liquordb.repository;

import com.liquordb.entity.Comment;
import com.liquordb.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 총 좋아요 개수 - 댓글 조회시 사용
    long countByComment_Id(Long commentId);

    // 유저가 좋아요 댓글 개수 - 마이페이지에서 사용
    long countByUser_IdAndCommentStatus(UUID userId, Comment.CommentStatus commentStatus);

    // 유저가 좋이요 누른 댓글 목록
    List<CommentLike> findByUser_IdAndCommentStatus(UUID userId, Comment.CommentStatus commentStatus);

    // 유저가 특정 댓글에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByComment_IdAndUser_Id(Long commentId, UUID userId);

    // 유저가 특정 댓글에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<CommentLike> findByUserIdAndCommentId(UUID userId, Long commentId);

    Optional<CommentLike> findByCommentIdAndUser_Id(Long commentId, UUID userId);
}
