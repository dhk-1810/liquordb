package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.CommentUpdateRequestDto;
import com.liquordb.entity.Comment;
import com.liquordb.exception.NotFoundException;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto create(User user, CommentRequestDto dto) {
        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        Comment parent = null;
        if (dto.getParentId() != null) { // 대댓글일 경우
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 부모댓글입니다."));
        }

        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .parent(parent)
                .content(dto.getContent())
                .status(Comment.CommentStatus.ACTIVE)
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto update(User user, Long commentId, CommentUpdateRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("권한이 없습니다. 댓글 수정은 댓글 작성자만 가능합니다.");
        }

        if (comment.getStatus().equals(Comment.CommentStatus.DELETED)) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.setContent(dto.getContent());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 특정 리뷰의 댓글 전체 조회 - 게시 중인 것만. 숨김, 삭제 제외.
    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> findByReviewId(Long reviewId, Pageable pageable) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        Page<Comment> commentPage
                = commentRepository.findByReviewIdAndStatus(reviewId, Comment.CommentStatus.ACTIVE, pageable);
        Page<CommentResponseDto> dtoPage = commentPage.map(CommentMapper::toDto);

        return PageResponse.from(dtoPage);
    }

    // 특정 유저가 쓴 댓글 전체 조회 - 게시 중인 것만. 숨김, 삭제 제외.
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findByUserIdAndStatus(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Page<Comment> comments = commentRepository.findByUserIdAndStatus(userId, Comment.CommentStatus.ACTIVE, pageable);
        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    // 댓글 삭제 (소프트 삭제)
    @Transactional
    public CommentResponseDto delete(Long commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다."); // 신고접수시 관리자는 삭제 X, 숨김 O
        }

        comment.setStatus(Comment.CommentStatus.DELETED);
        return CommentMapper.toDto(commentRepository.save(comment));
    }


    /**
     * 관리자용
     */

    // 유저ID, 리뷰 조회
    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> findAllByOptionalFilters(UUID userId, Comment.CommentStatus status, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Page<CommentResponseDto> page = commentRepository.findAllByOptionalFilters(userId, status, pageable)
                .map(CommentMapper::toDto);
        return PageResponse.from(page);
    }
}
