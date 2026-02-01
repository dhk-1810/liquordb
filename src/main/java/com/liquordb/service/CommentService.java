package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.CommentUpdateRequestDto;
import com.liquordb.entity.Comment;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.ReviewNotFoundException;
import com.liquordb.exception.user.UnauthenticatedUserException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.repository.CommentRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final CommentMapper commentMapper;

    // 댓글 생성
    @Transactional
    public CommentResponseDto create(User requestUser, Long reviewId, CommentRequestDto request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Long parentId = request.parentId();
        Comment parent = null;
        if (parentId != null) { // 답글일 경우
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new CommentNotFoundException(parentId));
        }

        Comment comment = commentMapper.toEntity(requestUser, request);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto update(User user, Long commentId, CommentUpdateRequestDto request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthenticatedUserException(user.getId());
        }

        if (comment.getStatus().equals(Comment.CommentStatus.DELETED)) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.update(request);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    // 특정 리뷰의 댓글 전체 조회 - 게시 중인 것만. 숨김, 삭제 제외.
    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> findByReviewId(Long reviewId, Pageable pageable) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Page<Comment> commentPage
                = commentRepository.findByReviewIdAndStatus(reviewId, Comment.CommentStatus.ACTIVE, pageable);
        Page<CommentResponseDto> dtoPage = commentPage.map(commentMapper::toDto);

        return PageResponse.from(dtoPage);
    }

    // 특정 유저가 쓴 댓글 전체 조회 - 게시 중인 것만. 숨김, 삭제 제외.
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findByUserId(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Page<Comment> comments = commentRepository.findByUserIdAndStatus(userId, Comment.CommentStatus.ACTIVE, pageable);
        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    // 댓글 삭제 (Soft Delete)
    @Transactional
    public void deleteByIdAndUser(Long commentId, User requestUser) {
        Comment comment = commentRepository.findByIdAndStatus_Active(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        UUID requestUserId = requestUser.getId();
        if (!comment.getUser().getId().equals(requestUserId)) {
            throw new UnauthenticatedUserException(requestUserId);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Page<CommentResponseDto> page = commentRepository.findAllByOptionalFilters(userId, status, pageable)
                .map(commentMapper::toDto);
        return PageResponse.from(page);
    }

    // 댓글 수동 숨기기 처리 (신고 누적 시 숨기기 처리는 자동)
    @Transactional
    public void hide(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        comment.hide(LocalDateTime.now());
    }

    // 댓글 숨기기 해제
    @Transactional
    public void unhide(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        comment.unhide();
    }
}
