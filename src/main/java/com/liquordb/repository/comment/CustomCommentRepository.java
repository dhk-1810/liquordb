package com.liquordb.repository.comment;

import com.liquordb.dto.comment.request.CommentListGetRequest;
import com.liquordb.dto.comment.request.CommentSearchRequest;
import com.liquordb.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CustomCommentRepository {

    // 특정 리뷰에 달린 댓글 조회
    Slice<Comment> findByReviewIdAndStatus(Long reviewId, CommentListGetRequest request);

    // 특정 유저가 작성한 댓글 조회
    Page<Comment> findByUserIdAndStatus(UUID userId, CommentListGetRequest request);

    // [관리자용] 댓글 전체 조회 - 유저별로 필터링(선택), 상태별로 필터링(선택)
    Page<Comment> findAll(CommentSearchRequest request);
}
