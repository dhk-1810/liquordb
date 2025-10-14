package com.liquordb.repository;

import com.liquordb.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 총 좋아요 개수 - 댓글 조회시 사용
    long countByCommentId(Long commentId);

    // 유저가 좋아요 댓글 개수 - 마이페이지에서 사용
    long countByUserId(UUID user_id);

    // 유저가 좋이요 누른 댓글 목록  - 마이페이지에서 사용
    List<CommentLike> findByUserId(UUID user_id);

    // 유저가 특정 댓글에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByUserIdAndCommentId (UUID userId, Long commentId);

    // 유저가 특정 댓글에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<CommentLike> findByUserIdAndCommentId(UUID userId, Long commentId);
}
