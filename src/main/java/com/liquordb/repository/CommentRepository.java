package com.liquordb.repository;

import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 리뷰에 달린 댓글 조회 (삭제되지 않은 댓글만)
    Page<Comment> findAllByReviewAndIsDeletedFalse(Review review, Pageable pageable);

    // 특정 유저가 작성한 댓글 수
    long countByUserAndIsHiddenFalse(User user);

    // 특정 유저가 작성한 댓글 조회 (삭제한 댓글은 제외)
    Page<Comment> findByUserIdAndIsHiddenFalseAndIsDeletedFalse(Pageable pageable, UUID UserId);
}
