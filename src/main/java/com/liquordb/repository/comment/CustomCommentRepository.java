package com.liquordb.repository.comment;

import com.liquordb.entity.Comment;
import com.liquordb.repository.comment.condition.CommentListGetCondition;
import com.liquordb.repository.comment.condition.CommentSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public interface CustomCommentRepository {

    // 특정 리뷰에 달린 댓글 조회
    Slice<Comment> findByReviewId(CommentListGetCondition condition);

    // 특정 유저가 작성한 댓글 조회
    Slice<Comment> findByUserId(CommentListGetCondition condition);

    // [관리자용] 댓글 전체 조회 - 유저별로 필터링(선택), 상태별로 필터링(선택)
    Page<Comment> findAll(CommentSearchCondition condition);
}
