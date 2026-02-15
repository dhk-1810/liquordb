package com.liquordb.service;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.PageResponse;
import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.CommentUpdateRequestDto;
import com.liquordb.entity.Comment;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.comment.CommentAccessDeniedException;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.comment.InvalidParentCommentException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CommentResponseDto create(Long reviewId, CommentRequestDto request, UUID userId) {

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
    public CommentResponseDto update(Long commentId, CommentUpdateRequestDto request, UUID userId) {

        Comment comment = commentRepository.findByIdAndStatus(commentId, Comment.CommentStatus.ACTIVE)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException(commentId, userId);
        }

        comment.update(request);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 특정 리뷰의 댓글 전체 조회 - 게시 중인 것만. 숨김, 삭제 제외.
    // TODO 정렬조건 - 인기순, 최신순
    @Transactional(readOnly = true)
    public CursorPageResponse<CommentResponseDto> findByReviewId(Long reviewId, Pageable pageable) {
        Review review = reviewRepository.findByIdAndStatus(reviewId, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Slice<Comment> comments
                = commentRepository.findByReview_IdAndStatus(reviewId, Comment.CommentStatus.ACTIVE, pageable);
        Slice<CommentResponseDto> response = comments.map(CommentMapper::toDto);

        Long nextCursor = null;
        if (response.hasNext()) {
            List<CommentResponseDto> content = response.getContent();
            nextCursor = content.get(content.size() - 1).id();
        }

        return CursorPageResponse.from(response, nextCursor);
    }

    // 본인이 쓴 댓글 전체 조회 - 게시 중인 것만. 숨김, 삭제 제외.
    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> findByUserId(UUID userId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByUserIdAndStatus(userId, Comment.CommentStatus.ACTIVE, pageable);
        Page<CommentResponseDto> response = comments.map(CommentMapper::toDto);
        return PageResponse.from(response);
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
    public PageResponse<CommentResponseDto> findAllByOptionalFilters(UUID userId, Comment.CommentStatus status, Pageable pageable) {
        if (userId != null) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        }
        Page<CommentResponseDto> page = commentRepository.findAllByOptionalFilters(userId, status, pageable)
                .map(CommentMapper::toDto);
        return PageResponse.from(page);
    }

}
