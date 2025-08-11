package com.liquordb.review.repository;

import com.liquordb.review.dto.CommentResponseDto;
import com.liquordb.review.entity.Comment;
import com.liquordb.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 리뷰에 달린 댓글 조회 (삭제되지 않은 댓글만)
    List<CommentResponseDto> findAllByReviewAndIsDeletedFalse(Review review);

    // 특정 유저가 작성한 댓글 수
    long countByUserId(Long userId);

    // 특정 유저가 작성한 댓글 목록
    @Query("SELECT DISTINCT c.review.liquor.id FROM Comment c WHERE c.user.id = :userId")
    List<Long> findDistinctLiquorIdsByUserId(@Param("userId") Long userId);

    // 특정 댓글이 부모 댓글로 달린 자식 댓글이 존재하는지
    boolean existsByParentId(Long parentId);

    /**
     * 이하는 관리자용 메서드입니다.
     */

    // 특정 유저가 작성한 댓글 조회 (삭제 댓글 포함)
    List<Comment> findByUserId(Long UserId);

}
