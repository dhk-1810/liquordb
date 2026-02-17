package com.liquordb.service;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.PageResponse;
import com.liquordb.repository.comment.condition.CommentListGetCondition;
import com.liquordb.repository.comment.condition.CommentSearchCondition;
import com.liquordb.dto.comment.request.CommentListGetRequest;
import com.liquordb.dto.comment.request.CommentRequest;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.request.CommentSearchRequest;
import com.liquordb.dto.comment.request.CommentUpdateRequest;
import com.liquordb.entity.Comment;
import com.liquordb.enums.SortCommentBy;
import com.liquordb.enums.SortDirection;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.comment.CommentAccessDeniedException;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.comment.InvalidParentCommentException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto create(Long reviewId, CommentRequest request, UUID userId) {

        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Review review = reviewRepository.findByIdAndStatus(reviewId, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Long parentId = request.parentId();
        Comment parent = null;
        if (parentId != null) { // 답글일 경우
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new InvalidParentCommentException(parentId));
            if (!parent.getReview().getId().equals(reviewId)) {
                throw new InvalidParentCommentException(parentId);
            }
        }

        Comment comment = CommentMapper.toEntity(request, parent, review, user);
        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto update(Long commentId, CommentUpdateRequest request, UUID userId) {

        Comment comment = commentRepository.findByIdAndStatus(commentId, Comment.CommentStatus.ACTIVE)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException(commentId, userId);
        }

        comment.update(request);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 특정 리뷰에 달린 댓글 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<CommentResponseDto> getByReviewId(Long reviewId, CommentListGetRequest request) {

        reviewRepository.findByIdAndStatus(reviewId, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        // 기본값 할당
        SortCommentBy sortBy = request.sortBy() == null ?  SortCommentBy.COMMENT_ID : request.sortBy();
        SortDirection sortDirection = request.sortDirection() == null ? SortDirection.DESC : request.sortDirection();

        boolean useId = sortBy == SortCommentBy.COMMENT_ID;
        if (!useId && request.idAfter() == null) {
            throw new InvalidParameterException(); // TODO 예외
        }

        CommentListGetCondition condition = CommentListGetCondition.builder()
                .reviewId(reviewId)
                .status(Comment.CommentStatus.ACTIVE)
                .cursor(request.cursor())
                .idAfter(request.idAfter())
                .limit(request.limit())
                .useId(useId)
                .descending(sortDirection == SortDirection.DESC)
                .build();

        Slice<Comment> comments = commentRepository.findByReviewId(condition);
        Slice<CommentResponseDto> response = comments.map(CommentMapper::toDto);

        Long nextCursor = null;
        if (response.hasNext()) {
            List<CommentResponseDto> content = response.getContent();
            nextCursor = content.get(content.size() - 1).id();
        }

        return CursorPageResponse.from(response, nextCursor);
    }

    // 특정 유저가 작성한 댓글 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<CommentResponseDto> getByUserId(UUID userId, CommentListGetRequest request) {

        SortDirection sortDirection = request.sortDirection() == null ? SortDirection.DESC : request.sortDirection();

        CommentListGetCondition condition = CommentListGetCondition.builder()
                .userId(userId)
                .status(Comment.CommentStatus.ACTIVE)
                .cursor(request.cursor())
                .limit(request.limit())
                .descending(sortDirection == SortDirection.DESC)
                .build();

        Slice<Comment> comments = commentRepository.findByUserId(condition);
        Slice<CommentResponseDto> response = comments.map(CommentMapper::toDto);

        Long nextCursor = null;
        if (response.hasNext()) {
            List<CommentResponseDto> content = response.getContent();
            nextCursor = content.get(content.size() - 1).id();
        }

        return CursorPageResponse.from(response, nextCursor);
    }

    // 댓글 삭제 (Soft Delete)
    @Transactional
    public void deleteByIdAndUser(Long commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndStatus(commentId, Comment.CommentStatus.ACTIVE)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException(commentId, userId);
        }
        comment.softDelete(LocalDateTime.now());
        commentRepository.save(comment);
    }

    /**
     * 관리자용
     */

    // 유저ID, 리뷰 조회
    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> getAll(CommentSearchRequest request) {
        CommentSearchCondition condition = getSearchCondition(request);
        Page<CommentResponseDto> page = commentRepository.findAll(condition)
                .map(CommentMapper::toDto);
        return PageResponse.from(page);
    }

    private CommentSearchCondition getSearchCondition(CommentSearchRequest request) {
        Comment.CommentStatus status = request.status() == null
                ? Comment.CommentStatus.ACTIVE
                : request.status();
        int page = request.page() == null
                ? 0
                : request.page();
        int limit = request.limit() == null
                ? 50
                : request.limit();
        boolean descending = request.sortDirection() == SortDirection.DESC;
        return new CommentSearchCondition(request.username(), status, page, limit, descending);
    }


}
